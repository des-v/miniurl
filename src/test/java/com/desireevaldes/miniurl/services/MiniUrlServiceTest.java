package com.desireevaldes.miniurl.services;

import com.desireevaldes.miniurl.models.MiniUrl;
import com.desireevaldes.miniurl.repositories.MiniUrlRepository;
import com.desireevaldes.miniurl.utils.CustomUrlValidator;
import com.desireevaldes.miniurl.utils.RandomKeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MiniUrlServiceTest {

    private static final String MINI_KEY = "abc1234";
    private static final String ORIGINAL_URL = "https://example.com";
    private static final String NEW_URL = "https://news.com";

    @Mock
    private MiniUrlRepository mockMiniUrlRepository;
    @Mock
    private CustomUrlValidator mockCustomUrlValidator;
    @Mock
    private RandomKeyGenerator mockRandomKeyGenerator;

    @InjectMocks
    private MiniUrlService miniUrlService;

    @Test
    public void testGetOrCreateMiniUrlAsync_ReturnsExisting() throws Exception {
        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey(MINI_KEY);
        existingMiniUrl.setFullUrl(ORIGINAL_URL);

        when(mockMiniUrlRepository.findByFullUrl(ORIGINAL_URL))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(existingMiniUrl)));

        MiniUrl result = miniUrlService.getOrCreateMiniUrlAsync(ORIGINAL_URL).get();

        assertEquals(existingMiniUrl.getMiniKey(), result.getMiniKey());
        verify(mockMiniUrlRepository, never()).save(any());
        verify(mockCustomUrlValidator).validateUrl(ORIGINAL_URL);
    }

    @Test
    public void testGetOrCreateMiniUrlAsync_CreatesNew() throws Exception {
        MiniUrl newMiniUrl = new MiniUrl();
        newMiniUrl.setMiniKey(MINI_KEY);
        newMiniUrl.setFullUrl(ORIGINAL_URL);

        when(mockMiniUrlRepository.findByFullUrl(ORIGINAL_URL))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(mockRandomKeyGenerator.generateKey()).thenReturn(MINI_KEY);
        when(mockMiniUrlRepository.existsByMiniKey(MINI_KEY))
                .thenReturn(CompletableFuture.completedFuture(false));
        when(mockMiniUrlRepository.save(any(MiniUrl.class))).thenReturn(newMiniUrl);

        MiniUrl result = miniUrlService.getOrCreateMiniUrlAsync(ORIGINAL_URL).get();

        assertEquals(MINI_KEY, result.getMiniKey());
        verify(mockMiniUrlRepository).save(any(MiniUrl.class));
        verify(mockCustomUrlValidator).validateUrl(ORIGINAL_URL);
        verify(mockRandomKeyGenerator).generateKey();
    }

    @Test
    public void testGetOrCreateMiniUrlAsync_ThrowsIfInvalidUrl() {
        String url = "invalid-url";

        doThrow(new IllegalArgumentException("Invalid URL format")).when(mockCustomUrlValidator).validateUrl(url);

        assertThrows(Exception.class, () -> {
            miniUrlService.getOrCreateMiniUrlAsync(url).get();
        });
        verify(mockCustomUrlValidator).validateUrl(url);
    }

    @Test
    public void testGetMiniUrlByMiniKey_ReturnsMiniUrl() throws Exception {
        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(MINI_KEY);
        miniUrl.setFullUrl(ORIGINAL_URL);

        when(mockMiniUrlRepository.findByMiniKey(MINI_KEY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));

        MiniUrl result = miniUrlService.getMiniUrlByMiniKey(MINI_KEY).get();

        assertEquals(MINI_KEY, result.getMiniKey());
    }

    @Test
    public void testGetMiniUrlByMiniKey_ThrowsIfNotFound() {
        String miniKey = "notfound";

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        assertThrows(Exception.class, () -> {
            miniUrlService.getMiniUrlByMiniKey(miniKey).get();
        });
    }

    @Test
    public void testUpdateMiniUrlAsync_UpdatesIfExists() throws Exception {
        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(MINI_KEY);
        miniUrl.setFullUrl(ORIGINAL_URL);

        when(mockMiniUrlRepository.findByMiniKey(MINI_KEY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));
        when(mockMiniUrlRepository.save(any(MiniUrl.class))).thenReturn(miniUrl);

        MiniUrl result = miniUrlService.updateMiniUrlAsync(MINI_KEY, NEW_URL).get();
        assertEquals(NEW_URL, result.getFullUrl());
        verify(mockCustomUrlValidator).validateUrl(NEW_URL);
    }

    @Test
    public void testUpdateMiniUrlAsync_FailsIfNotFound() {
        String miniKey = "notfound";

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        assertThrows(Exception.class, () -> {
            miniUrlService.updateMiniUrlAsync(miniKey, NEW_URL).get();
        });
        verify(mockCustomUrlValidator).validateUrl(NEW_URL);
    }

    @Test
    public void testDeleteMiniUrlAsync_DeletesIfExists() throws Exception {
        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(MINI_KEY);
        miniUrl.setFullUrl(ORIGINAL_URL);

        when(mockMiniUrlRepository.findByMiniKey(MINI_KEY))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));
        doNothing().when(mockMiniUrlRepository).deleteById(MINI_KEY);

        miniUrlService.deleteMiniUrlAsync(MINI_KEY).get();
        verify(mockMiniUrlRepository).deleteById(MINI_KEY);
    }

    @Test
    public void testDeleteMiniUrlAsync_FailsIfNotFound() {
        String miniKey = "notfound";

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        assertThrows(Exception.class, () -> {
            miniUrlService.deleteMiniUrlAsync(miniKey).get();
        });
    }

    @Test
    public void testGetAllMiniUrlsAsync_ReturnsAllMiniUrls() throws Exception {
        MiniUrl miniUrl1 = new MiniUrl();
        miniUrl1.setMiniKey("abd1234");
        miniUrl1.setFullUrl("https://google.com");

        MiniUrl miniUrl2 = new MiniUrl();
        miniUrl2.setMiniKey("zyx1234");
        miniUrl2.setFullUrl("https://bing.com");

        List<MiniUrl> allMiniUrls = List.of(miniUrl1, miniUrl2);

        when(mockMiniUrlRepository.findAll()).thenReturn(allMiniUrls);

        List<MiniUrl> result = miniUrlService.getAllMiniUrlsAsync().get();

        assertEquals(2, result.size());
        assertTrue(result.contains(miniUrl1));
        assertTrue(result.contains(miniUrl2));
        verify(mockMiniUrlRepository).findAll();
    }
}

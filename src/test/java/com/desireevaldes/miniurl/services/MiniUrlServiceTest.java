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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MiniUrlServiceTest {

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
        String fullUrl = "https://example.com";

        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey("abc1234");
        existingMiniUrl.setFullUrl(fullUrl);

        when(mockMiniUrlRepository.findByFullUrl(fullUrl))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(existingMiniUrl)));

        MiniUrl result = miniUrlService.getOrCreateMiniUrlAsync(fullUrl).get();

        assertEquals(existingMiniUrl.getMiniKey(), result.getMiniKey());
        verify(mockMiniUrlRepository, never()).save(any());
        verify(mockCustomUrlValidator).validateUrl(fullUrl);
    }

    @Test
    public void testGetOrCreateMiniUrlAsync_CreatesNew() throws Exception {
        String url = "https://example.com";
        String miniKey = "xyz1234";

        MiniUrl newMiniUrl = new MiniUrl();
        newMiniUrl.setMiniKey(miniKey);
        newMiniUrl.setFullUrl(url);

        when(mockMiniUrlRepository.findByFullUrl(url))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(mockRandomKeyGenerator.generateKey()).thenReturn(miniKey);
        when(mockMiniUrlRepository.existsByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(false));
        when(mockMiniUrlRepository.save(any(MiniUrl.class))).thenReturn(newMiniUrl);

        MiniUrl result = miniUrlService.getOrCreateMiniUrlAsync(url).get();

        assertEquals(miniKey, result.getMiniKey());
        verify(mockMiniUrlRepository).save(any(MiniUrl.class));
        verify(mockCustomUrlValidator).validateUrl(url);
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
        String url = "https://example.com";
        String miniKey = "xyz1234";

        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(miniKey);
        miniUrl.setFullUrl(url);

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));

        MiniUrl result = miniUrlService.getMiniUrlByMiniKey(miniKey).get();

        assertEquals(miniKey, result.getMiniKey());
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
        String url = "https://example.com";
        String miniKey = "xyz1234";

        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(miniKey);
        miniUrl.setFullUrl(url);

        String newUrl = "https://news.com";

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));
        when(mockMiniUrlRepository.save(any(MiniUrl.class))).thenReturn(miniUrl);

        MiniUrl result = miniUrlService.updateMiniUrlAsync(miniKey, newUrl).get();
        assertEquals(newUrl, result.getFullUrl());
        verify(mockCustomUrlValidator).validateUrl(newUrl);
    }

    @Test
    public void testUpdateMiniUrlAsync_FailsIfNotFound() {
        String miniKey = "notfound";
        String newUrl = "https://news.com";

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        assertThrows(Exception.class, () -> {
            miniUrlService.updateMiniUrlAsync(miniKey, newUrl).get();
        });
        verify(mockCustomUrlValidator).validateUrl(newUrl);
    }

    @Test
    public void testDeleteMiniUrlAsync_DeletesIfExists() throws Exception {
        String url = "https://example.com";
        String miniKey = "abc1234";

        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(miniKey);
        miniUrl.setFullUrl(url);

        when(mockMiniUrlRepository.findByMiniKey(miniKey))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(miniUrl)));
        doNothing().when(mockMiniUrlRepository).deleteById(miniKey);

        miniUrlService.deleteMiniUrlAsync(miniKey).get();
        verify(mockMiniUrlRepository).deleteById(miniKey);
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
}

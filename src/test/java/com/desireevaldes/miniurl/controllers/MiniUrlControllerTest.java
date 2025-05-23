package com.desireevaldes.miniurl.controllers;

import com.desireevaldes.miniurl.api.v1.MiniUrlController;
import com.desireevaldes.miniurl.dto.MiniUrlRequestDto;
import com.desireevaldes.miniurl.exceptions.MiniUrlNotFoundException;
import com.desireevaldes.miniurl.models.MiniUrl;
import com.desireevaldes.miniurl.services.MiniUrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MiniUrlController.class)
public class MiniUrlControllerTest {

    private static final String MINI_KEY = "abc1234";
    private static final String ORIGINAL_URL = "https://example.com";
    private static final String UPDATED_URL = "https://news.com";

    @MockitoBean
    private MiniUrlService mockMiniUrlService;

    @Autowired
    private MockMvc mockMvc;

    private String readJson(String filename) throws Exception {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/json/" + filename)));
    }

    @Test
    public void testCreateMiniUrl() throws Exception {
        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(MINI_KEY);
        miniUrl.setFullUrl(ORIGINAL_URL);
        miniUrl.setCreatedAt(Instant.now());
        miniUrl.setUpdatedAt(Instant.now());

        when(mockMiniUrlService.getOrCreateMiniUrlAsync(ORIGINAL_URL))
                .thenReturn(CompletableFuture.completedFuture(miniUrl));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/miniurls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("miniurl-create.json")))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.miniKey").value(miniUrl.getMiniKey()))
                .andExpect(jsonPath("$.fullUrl").value(miniUrl.getFullUrl()));
    }

    @Test
    public void testGetMiniUrl() throws Exception {
        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey(MINI_KEY);
        existingMiniUrl.setFullUrl(ORIGINAL_URL);
        existingMiniUrl.setCreatedAt(Instant.now());
        existingMiniUrl.setUpdatedAt(Instant.now());

        when(mockMiniUrlService.getMiniUrlByMiniKey(existingMiniUrl.getMiniKey()))
                .thenReturn(CompletableFuture.completedFuture(existingMiniUrl));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/miniurls/" + existingMiniUrl.getMiniKey()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.miniKey").value(existingMiniUrl.getMiniKey()))
                .andExpect(jsonPath("$.fullUrl").value(existingMiniUrl.getFullUrl()));
    }

    @Test
    public void testUpdateMiniUrl() throws Exception {
        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey(MINI_KEY);
        existingMiniUrl.setFullUrl(ORIGINAL_URL);
        existingMiniUrl.setCreatedAt(Instant.now());
        existingMiniUrl.setUpdatedAt(Instant.now());

        MiniUrl updatedMiniUrl = new MiniUrl();
        updatedMiniUrl.setMiniKey(MINI_KEY);
        updatedMiniUrl.setFullUrl(UPDATED_URL);
        updatedMiniUrl.setCreatedAt(existingMiniUrl.getCreatedAt());
        updatedMiniUrl.setUpdatedAt(Instant.now());

        when(mockMiniUrlService.updateMiniUrlAsync(existingMiniUrl.getMiniKey(), UPDATED_URL))
                .thenReturn(CompletableFuture.completedFuture(updatedMiniUrl));

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/miniurls/" + existingMiniUrl.getMiniKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("miniurl-update.json")))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.miniKey").value(updatedMiniUrl.getMiniKey()))
                .andExpect(jsonPath("$.fullUrl").value(updatedMiniUrl.getFullUrl()));
    }

    @Test
    public void testDeleteMiniUrl() throws Exception {
        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey(MINI_KEY);
        existingMiniUrl.setFullUrl(ORIGINAL_URL);
        existingMiniUrl.setCreatedAt(Instant.now());
        existingMiniUrl.setUpdatedAt(Instant.now());

        when(mockMiniUrlService.deleteMiniUrlAsync(existingMiniUrl.getMiniKey()))
                .thenReturn(CompletableFuture.completedFuture(null));

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/miniurls/" + existingMiniUrl.getMiniKey()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllMiniUrls_Empty() throws Exception {
        when(mockMiniUrlService.getAllMiniUrlsAsync())
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/miniurls"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    public void testGetAllMiniUrls() throws Exception {
        MiniUrl miniUrl1 = new MiniUrl();
        miniUrl1.setMiniKey(MINI_KEY);
        miniUrl1.setFullUrl(ORIGINAL_URL);
        miniUrl1.setCreatedAt(Instant.now());
        miniUrl1.setUpdatedAt(Instant.now());

        MiniUrl miniUrl2 = new MiniUrl();
        miniUrl2.setMiniKey("zyx1234");
        miniUrl2.setFullUrl("https://bing.com");
        miniUrl2.setCreatedAt(Instant.now());
        miniUrl2.setUpdatedAt(Instant.now());

        List<MiniUrl> allMiniUrls = List.of(miniUrl1, miniUrl2);

        when(mockMiniUrlService.getAllMiniUrlsAsync())
                .thenReturn(CompletableFuture.completedFuture(allMiniUrls));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/miniurls"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].miniKey", is(MINI_KEY)))
                .andExpect(jsonPath("$[0].fullUrl", is(ORIGINAL_URL)))
                .andExpect(jsonPath("$[1].miniKey", is("zyx1234")))
                .andExpect(jsonPath("$[1].fullUrl", is("https://bing.com")));
    }

    @Test
    public void testGetMiniUrl_NotFound() throws Exception {
        String notFoundMiniKey = "doesnotexist";

        when(mockMiniUrlService.getMiniUrlByMiniKey(notFoundMiniKey))
                .thenReturn(CompletableFuture.failedFuture(new MiniUrlNotFoundException("Not found")));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/miniurls/" + notFoundMiniKey))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteMiniUrl_NotFound() throws Exception {
        String notFoundMiniKey = "doesnotexist";

        when(mockMiniUrlService.deleteMiniUrlAsync(notFoundMiniKey))
                .thenReturn(CompletableFuture.failedFuture(new MiniUrlNotFoundException("Not found")));

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/miniurls/" + notFoundMiniKey))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMiniUrl_InvalidUrl() throws Exception {
        String invalidUrl = "not-a-url";

        MiniUrlRequestDto requestDto = new MiniUrlRequestDto();
        requestDto.setFullUrl(invalidUrl);

        when(mockMiniUrlService.getOrCreateMiniUrlAsync(invalidUrl))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid URL")));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/miniurls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("invalid-miniurl.json")))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid URL")));
    }

    @Test
    public void testUpdateMiniUrl_InvalidUrl() throws Exception {
        String invalidUrl = "not-a-url";

        MiniUrl existingMiniUrl = new MiniUrl();
        existingMiniUrl.setMiniKey(MINI_KEY);
        existingMiniUrl.setFullUrl(ORIGINAL_URL);
        existingMiniUrl.setCreatedAt(Instant.now());
        existingMiniUrl.setUpdatedAt(Instant.now());

        when(mockMiniUrlService.updateMiniUrlAsync(eq(existingMiniUrl.getMiniKey()), eq(invalidUrl)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid URL")));

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/miniurls/" + existingMiniUrl.getMiniKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("invalid-miniurl.json")))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid URL")));
    }
}

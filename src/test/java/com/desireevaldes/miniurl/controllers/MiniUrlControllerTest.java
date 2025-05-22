package com.desireevaldes.miniurl.controllers;

import com.desireevaldes.miniurl.api.v1.MiniUrlController;
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
import java.util.concurrent.CompletableFuture;

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
}

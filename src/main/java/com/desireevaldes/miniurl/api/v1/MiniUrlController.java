package com.desireevaldes.miniurl.api.v1;

import com.desireevaldes.miniurl.dto.MiniUrlRequestDto;
import com.desireevaldes.miniurl.dto.MiniUrlResponseDto;
import com.desireevaldes.miniurl.models.MiniUrl;
import com.desireevaldes.miniurl.services.MiniUrlService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/miniurls")
public class MiniUrlController {
    private static final Logger logger = LoggerFactory.getLogger(MiniUrlController.class);

    private final MiniUrlService miniUrlService;

    public MiniUrlController(MiniUrlService miniUrlService) {
        this.miniUrlService = miniUrlService;
    }

    private static MiniUrlResponseDto toResponseDto(MiniUrl miniUrl) {
        MiniUrlResponseDto responseDto = new MiniUrlResponseDto();
        responseDto.setMiniKey(miniUrl.getMiniKey());
        responseDto.setFullUrl(miniUrl.getFullUrl());
        responseDto.setCreatedAt(miniUrl.getCreatedAt());
        responseDto.setUpdatedAt(miniUrl.getUpdatedAt());
        return responseDto;
    }

    @PostMapping
    public CompletableFuture<MiniUrlResponseDto> createMiniUrl(@Valid @RequestBody MiniUrlRequestDto requestDto) {
        logger.info("Received POST request to create or get if exists mini-URL for URL: {}", requestDto.getFullUrl());
        return miniUrlService.getOrCreateMiniUrlAsync(requestDto.getFullUrl())
                .thenApply(miniUrl -> toResponseDto(miniUrl));
    }

    @GetMapping("/{miniKey}")
    public CompletableFuture<MiniUrlResponseDto> getMiniUrl(@PathVariable String miniKey) {
        logger.info("Received GET request to get mini-URL with miniKey: {}", miniKey);
        return miniUrlService.getMiniUrlByMiniKey(miniKey)
                .thenApply(miniUrl -> toResponseDto(miniUrl));
    }

    @PutMapping("/{miniKey}")
    public CompletableFuture<MiniUrlResponseDto> updateMiniUrl(@PathVariable String miniKey,
                                                               @Valid @RequestBody MiniUrlRequestDto requestDto) {
        logger.info("Received PUT request to update mini-URL with miniKey: {}", miniKey);
        return miniUrlService.updateMiniUrlAsync(miniKey, requestDto.getFullUrl())
                .thenApply(miniUrl -> toResponseDto(miniUrl));
    }

    @DeleteMapping("/{miniKey}")
    public CompletableFuture<Void> deleteMiniUrl(@PathVariable String miniKey) {
        logger.info("Received DELETE request to delete mini-URL with miniKey: {}", miniKey);
        return miniUrlService.deleteMiniUrlAsync(miniKey);
    }

    @GetMapping
    public CompletableFuture<List<MiniUrlResponseDto>> getAllMiniUrls() {
        logger.info("Received GET request to list all mini-URLs");
        return miniUrlService.getAllMiniUrlsAsync()
                .thenApply(miniUrls -> miniUrls.stream()
                        .map(miniUrl -> MiniUrlController.toResponseDto(miniUrl))
                        .toList());
    }
}

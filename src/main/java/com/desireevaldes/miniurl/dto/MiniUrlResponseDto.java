package com.desireevaldes.miniurl.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

public class MiniUrlResponseDto {
    private static final Logger logger = LoggerFactory.getLogger(MiniUrlResponseDto.class);

    private String miniKey;
    private String fullUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public String getMiniKey() {
        return miniKey;
    }

    public void setMiniKey(String miniKey) {
        this.miniKey = miniKey;
        logger.debug("Set miniKey: {}", miniKey);
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
        logger.debug("Set fullUrl: {}", fullUrl);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MiniUrlResponseDto that = (MiniUrlResponseDto) o;
        return Objects.equals(miniKey, that.miniKey) && Objects.equals(fullUrl, that.fullUrl) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(miniKey, fullUrl, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "MiniUrlResponseDto{" +
                "miniKey='" + miniKey + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

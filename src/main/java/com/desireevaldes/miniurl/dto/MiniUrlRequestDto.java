package com.desireevaldes.miniurl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MiniUrlRequestDto {
    private static final Logger logger = LoggerFactory.getLogger(MiniUrlRequestDto.class);

    @NotBlank(message = "Url must not be blank")
    @Size(max = 2048, message = "Url must not exceed 2048 characters")
    private String fullUrl;

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
        logger.debug("Set fullUrl {}", fullUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MiniUrlRequestDto that = (MiniUrlRequestDto) o;
        return Objects.equals(fullUrl, that.fullUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fullUrl);
    }

    @Override
    public String toString() {
        return "MiniUrlRequestDto{" +
                "fullUrl='" + fullUrl + '\'' +
                '}';
    }
}

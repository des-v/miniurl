package com.desireevaldes.miniurl.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "mini_urls")
public class MiniUrl {
    private static final Logger logger = LoggerFactory.getLogger(MiniUrl.class);

    @Id
    @Column(length = 32)
    @Size(min = 7, max = 32, message = "MiniKey must be between 7 and 32 characters")
    private String miniKey;

    @Column(nullable = false, length = 2048)
    @NotBlank(message = "Url must not be blank")
    @Size(max = 2048, message = "Url must not exceed 2048 characters")
    private String fullUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = Instant.now();
        logger.debug("MiniUrl created: {}", this);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        logger.debug("MiniUrl updated: {}", this);
    }

    public String getMiniKey() {
        return miniKey;
    }

    public void setMiniKey(String miniKey) {
        this.miniKey = miniKey;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
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
        MiniUrl miniUrl = (MiniUrl) o;
        return Objects.equals(miniKey, miniUrl.miniKey) && Objects.equals(fullUrl, miniUrl.fullUrl) && Objects.equals(createdAt, miniUrl.createdAt) && Objects.equals(updatedAt, miniUrl.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(miniKey, fullUrl, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "MiniUrl{" +
                "miniKey='" + miniKey + '\'' +
                ", fullUrl='" + fullUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

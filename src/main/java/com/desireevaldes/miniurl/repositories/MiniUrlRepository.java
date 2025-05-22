package com.desireevaldes.miniurl.repositories;

import com.desireevaldes.miniurl.models.MiniUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface MiniUrlRepository extends JpaRepository<MiniUrl, String> {

    @Async
    CompletableFuture<Optional<MiniUrl>> findByFullUrl(String fullUrl);

    @Async
    CompletableFuture<Boolean> existsByFullUrl(String fullUrl);

    @Async
    CompletableFuture<Boolean> existsByMiniKey(String miniKey);
}

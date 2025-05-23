package com.desireevaldes.miniurl.services;

import com.desireevaldes.miniurl.exceptions.MiniUrlNotFoundException;
import com.desireevaldes.miniurl.models.MiniUrl;
import com.desireevaldes.miniurl.repositories.MiniUrlRepository;
import com.desireevaldes.miniurl.utils.CustomUrlValidator;
import com.desireevaldes.miniurl.utils.RandomKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class MiniUrlService {
    private static final Logger logger = LoggerFactory.getLogger(MiniUrlService.class);

    private final MiniUrlRepository miniUrlRepository;
    private final CustomUrlValidator customUrlValidator;
    private final RandomKeyGenerator randomKeyGenerator;

    public MiniUrlService(MiniUrlRepository miniUrlRepository, CustomUrlValidator customUrlValidator, RandomKeyGenerator randomKeyGenerator) {
        this.miniUrlRepository = miniUrlRepository;
        this.customUrlValidator = customUrlValidator;
        this.randomKeyGenerator = randomKeyGenerator;
    }

    @Transactional
    public CompletableFuture<MiniUrl> getOrCreateMiniUrlAsync(String fullUrl) {
        try {
            customUrlValidator.validateUrl(fullUrl);

            return findMiniUrlByFullUrlAsync(fullUrl)
                    .thenCompose(existingMiniUrl -> {
                        MiniUrl url = existingMiniUrl.orElse(null);
                        if (url != null) {
                            logger.info("MiniUrl already exists for this Url.");
                            return CompletableFuture.completedFuture(url);
                        } else {
                            return createAndSaveMiniUrlAsync(fullUrl);
                        }
                    });

        } catch (Exception exception) {
            CompletableFuture<MiniUrl> failedResult = new CompletableFuture<>();
            failedResult.completeExceptionally(exception);
            return failedResult;
        }
    }

    private CompletableFuture<Optional<MiniUrl>> findMiniUrlByMiniKeyAsync(String miniKey) {
        return miniUrlRepository.findByMiniKey(miniKey);
    }

    private CompletableFuture<Optional<MiniUrl>> findMiniUrlByFullUrlAsync(String fullUrl) {
        return miniUrlRepository.findByFullUrl(fullUrl);
    }

    private CompletableFuture<MiniUrl> createAndSaveMiniUrlAsync(String fullUrl) {
        return generateUniqueMiniKeyAsync()
                .thenCompose(miniKey -> buildAndSaveMiniUrl(miniKey, fullUrl));
    }

    private CompletableFuture<String> generateUniqueMiniKeyAsync() {
        String newMiniKey = randomKeyGenerator.generateKey();
        return checkIfMiniKeyExistsAsync(newMiniKey)
                .thenCompose(exists -> handleMiniKeyAlreadyExists(exists, newMiniKey)
        );
    }

    private CompletableFuture<Boolean> checkIfMiniKeyExistsAsync(String miniKey) {
        return miniUrlRepository.existsByMiniKey(miniKey);
    }

    private CompletableFuture<String> handleMiniKeyAlreadyExists(boolean exists, String miniKey) {
        if (exists) {
            logger.debug("MiniKey already exists: {}, regenerating a new miniKey...", miniKey);
            return generateUniqueMiniKeyAsync();
        } else {
            return CompletableFuture.completedFuture(miniKey);
        }
    }

    private CompletableFuture<MiniUrl> buildAndSaveMiniUrl(String miniKey, String fullUrl) {
        MiniUrl miniUrl = new MiniUrl();
        miniUrl.setMiniKey(miniKey);
        miniUrl.setFullUrl(fullUrl);
        logger.info("Saving new MiniUrl: {}", miniUrl);
        return CompletableFuture.completedFuture(miniUrlRepository.save(miniUrl));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<MiniUrl> getMiniUrlByMiniKey(String miniKey) {
        return findMiniUrlByMiniKeyAsync(miniKey)
                .thenCompose(optionalMiniUrl -> {
                    MiniUrl url = optionalMiniUrl.orElse(null);
                    if (url != null) {
                        return CompletableFuture.completedFuture(url);
                    } else {
                        CompletableFuture<MiniUrl> failedResult = new CompletableFuture<>();
                        failedResult.completeExceptionally(new MiniUrlNotFoundException("MiniUrl not found with miniKey: " + miniKey));
                        return failedResult;
                    }
                });
    }

    @Transactional
    public CompletableFuture<MiniUrl> updateMiniUrlAsync(String miniKey, String newFullUrl) {
        try {
            customUrlValidator.validateUrl(newFullUrl);

            return findMiniUrlByMiniKeyAsync(miniKey)
                    .thenCompose(optionalMiniUrl -> {
                        MiniUrl miniUrl = optionalMiniUrl.orElse(null);
                        if (miniUrl == null) {
                            CompletableFuture<MiniUrl> failedResult = new CompletableFuture<>();
                            failedResult.completeExceptionally(
                                    new MiniUrlNotFoundException("MiniUrl not found with miniKey: " + miniKey));
                            return failedResult;
                        }
                        miniUrl.setFullUrl(newFullUrl);
                        logger.info("Updating MiniUrl with miniKey: {}", miniUrl);
                        MiniUrl savedMiniUrl = miniUrlRepository.save(miniUrl);
                        return CompletableFuture.completedFuture(savedMiniUrl);
                    });

        } catch (Exception exception) {
            CompletableFuture<MiniUrl> failedResult = new CompletableFuture<>();
            failedResult.completeExceptionally(exception);
            return failedResult;
        }
    }

    @Transactional
    public CompletableFuture<Void> deleteMiniUrlAsync(String miniKey) {
        return findMiniUrlByMiniKeyAsync(miniKey)
                .thenCompose(optionalMiniUrl -> {
                    MiniUrl miniUrl = optionalMiniUrl.orElse(null);
                    if (miniUrl == null) {
                        CompletableFuture<Void> failedResult = new CompletableFuture<>();
                        failedResult.completeExceptionally(
                                new MiniUrlNotFoundException("MiniUrl not found with miniKey: " + miniKey));
                        return failedResult;
                    }
                    logger.info("Deleting MiniUrl with miniKey: {}", miniKey);
                    miniUrlRepository.deleteById(miniKey);
                    return CompletableFuture.completedFuture(null);
                });
    }

    @Transactional(readOnly = true)
    public CompletableFuture<List<MiniUrl>> getAllMiniUrlsAsync() {
        logger.info("Fetching all MiniUrls");
        return CompletableFuture.supplyAsync(() -> miniUrlRepository.findAll());
    }
}

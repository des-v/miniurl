package com.desireevaldes.miniurl.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomKeyGenerator {
    private final SecureRandom random = new SecureRandom();
    private final String customAlphabet;
    private final int keyLength;
    private static final int MINIURL_MIN_KEY_LENGTH = 7;

    public RandomKeyGenerator(
            @Value("${MINIURL_CUSTOM_ALPHABET:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz}") String customAlphabet,
            @Value("${MINIURL_KEY_LENGTH:8}") int keyLength
    ) {
        this.customAlphabet = customAlphabet;
        this.keyLength = validateKeyLength(keyLength);
    }

    private int validateKeyLength(int keyLength) {
        if (keyLength < MINIURL_MIN_KEY_LENGTH) {
            keyLength = MINIURL_MIN_KEY_LENGTH;
        }
        return keyLength;
    }

    public String generateKey() {
        StringBuilder newKey = new StringBuilder(keyLength);
        for (int i = 0; i < keyLength; i++) {
            int index = random.nextInt(customAlphabet.length());
            newKey.append(customAlphabet.charAt(index));
        }
        return newKey.toString();
    }
}

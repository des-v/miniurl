package com.desireevaldes.miniurl.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomKeyGeneratorTest {

    String customAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    String newCustomAlphabet = "0123456789ABCDEFG";

    @Test
    public void testKeyMinimumLength() {
        RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator(customAlphabet, 6);
        String newKey = randomKeyGenerator.generateKey();
        assertEquals(7, newKey.length(), "The key length should be at least the minimum (7)");
    }

    @Test
    public void testCustomKeyLength() {
        RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator(customAlphabet, 10);
        String newKey = randomKeyGenerator.generateKey();
        assertEquals(10, newKey.length(), "The key should match the custom value (10)");
    }

    @Test
    public void testGeneratedKeyUsesACustomAlphabet() {
        RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator(newCustomAlphabet, 8);
        String newKey = randomKeyGenerator.generateKey();
        assertTrue(newKey.matches("[0123456789ABCDEFG]+"), "The key length should contain characters from the new custom alphabet");
    }

    @Test
    public void testKeyIsUnique() {
        RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator(customAlphabet, 10);
        String newKey1 = randomKeyGenerator.generateKey();
        String newKey2 = randomKeyGenerator.generateKey();
        assertNotEquals(newKey1, newKey2, "The random generated keys should mot be the same");
    }
}

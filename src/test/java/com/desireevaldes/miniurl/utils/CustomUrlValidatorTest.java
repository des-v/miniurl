package com.desireevaldes.miniurl.utils;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUrlValidatorTest {

    @Mock
    private UrlValidator mockUrlValidator;

    @InjectMocks
    private CustomUrlValidator customUrlValidator;

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com", "https://google.com"})
    public void testValidUrls(String url) {
        when(mockUrlValidator.isValid(url)).thenReturn(true);

        boolean result = customUrlValidator.validateUrl(url);

        assertTrue(result);
        verify(mockUrlValidator, times(1)).isValid(url);
    }

    @Test
    public void testInvalidUrl() {
        String invalidUrl = "invalid-url";
        when(mockUrlValidator.isValid(invalidUrl)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customUrlValidator.validateUrl(invalidUrl)
        );

        assertEquals("Invalid URL format", exception.getMessage());

        verify(mockUrlValidator).isValid(invalidUrl);
    }
}

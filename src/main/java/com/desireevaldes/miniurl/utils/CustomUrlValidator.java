package com.desireevaldes.miniurl.utils;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomUrlValidator {

    private static final Logger logger = LoggerFactory.getLogger(CustomUrlValidator.class);

    private final UrlValidator urlValidator;

    public CustomUrlValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public boolean validateUrl(String url) {
        logger.debug("Validating URL: {}", url);

        if(!urlValidator.isValid(url)) {
            logger.warn("Invalid URL format: {}", url);
            throw new IllegalArgumentException("Invalid URL format");
        }

        logger.info("URL is valid: {}", url);
        return true;
    }
}

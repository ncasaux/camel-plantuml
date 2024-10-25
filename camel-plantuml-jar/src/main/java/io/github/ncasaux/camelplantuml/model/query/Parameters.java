package io.github.ncasaux.camelplantuml.model.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Parameters {
    private static final Logger LOGGER = LoggerFactory.getLogger(Parameters.class);
    private static final String MATCH_NOTHING_PATTERN_STRING = "(?!.*)";

    private final boolean connectRoutes;

    private final Pattern uriFilterPattern;

    public Parameters(boolean connectRoutes,
                      String uriFilterPatternString) {
        this.connectRoutes = connectRoutes;

        Pattern validUriFilterPattern;
        try {
            validUriFilterPattern = Pattern.compile(URLDecoder.decode(Objects.requireNonNullElse(uriFilterPatternString, MATCH_NOTHING_PATTERN_STRING),
                    StandardCharsets.UTF_8));
            LOGGER.info("Value for uriFilterPattern \"{}\"", uriFilterPatternString);
        } catch (PatternSyntaxException e) {
            LOGGER.info("Invalid value for uriFilterPattern \"{}\"; will be deactivated", uriFilterPatternString);
            validUriFilterPattern = Pattern.compile(MATCH_NOTHING_PATTERN_STRING);
        }

        this.uriFilterPattern = validUriFilterPattern;
    }

    public boolean connectRoutes() {
        return connectRoutes;
    }

    public Pattern uriFilterPattern() {
        return uriFilterPattern;
    }
}

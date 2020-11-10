package fr.ncasaux.camelplantuml.utils;

import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class EndpointUtils {

    public static String getEndpointBaseUri(String endpointUri, Logger LOGGER) throws URISyntaxException, UnsupportedEncodingException {

        URI uri = new URI(endpointUri);
        String endpointBaseUri = new URI(
                uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null,
                uri.getFragment()).toString();

        LOGGER.info("EndpointBaseUri \"{}\" built from EndpointUri \"{}\"",
                URLDecoder.decode(endpointBaseUri,"UTF-8"),
                URLDecoder.decode(endpointUri,"UTF-8"));

        return endpointBaseUri;
    }
}

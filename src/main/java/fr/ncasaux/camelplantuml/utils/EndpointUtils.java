package fr.ncasaux.camelplantuml.utils;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;

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

    public static void addEndpointBaseUriInfo(HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                              String endpointBaseUri,
                                              EndpointBaseUriInfo endpointBaseUriInfo,
                                              Logger LOGGER) {

        endpointBaseUriInfo.setDiagramElementId("endpoint_".concat(String.valueOf(endpointBaseUrisInfo.size())));

        if (!endpointBaseUrisInfo.containsKey(endpointBaseUri)) {
            endpointBaseUrisInfo.put(endpointBaseUri, endpointBaseUriInfo);
            LOGGER.info("EndpointBaseUri with id \"{}\" added to the map of endpointBaseUris", endpointBaseUri);
        } else {
            LOGGER.info("EndpointBaseUri with id \"{}\" already in the map of endpointBaseUris", endpointBaseUri);
        }
    }
}

package fr.ncasaux.camelplantuml.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class EndpointUriInfo {
    private final String endpointBaseUri;

    public EndpointUriInfo(String endpointBaseUri) throws URISyntaxException {
        URI uri = new URI(endpointBaseUri);
        this.endpointBaseUri = URLDecoder.decode(new URI(
                uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null,
                uri.getFragment()).toString(), StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return "EndpointUri with endpointBaseUri \"".concat(endpointBaseUri).concat("\"");
    }

    public String getEndpointBaseUri() {
        return endpointBaseUri;
    }
}

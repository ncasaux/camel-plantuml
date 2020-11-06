package fr.ncasaux.camelplantuml.model;

public class EndpointUriInfo {
    private String endpointBaseUri;

    @Override
    public String toString() {
        return "EndpointUri with endpointBaseUri \"".concat(endpointBaseUri).concat("\"");
    }

    public String getEndpointBaseUri() {
        return endpointBaseUri;
    }

    public void setEndpointBaseUri(String endpointBaseUri) {
        this.endpointBaseUri = endpointBaseUri;
    }
}

package fr.ncasaux.camelplantuml.model;

public class RouteInfo {
    private final String description;
    private final String diagramElementId;
    private final String endpointBaseUri;

    public RouteInfo(String description, String diagramElementId, String endpointBaseUri) {
        this.description = description;
        this.diagramElementId = diagramElementId;
        this.endpointBaseUri = endpointBaseUri;
    }

    @Override
    public String toString() {
        return "Route with description \"".concat(description).concat("\"");
    }

    public String getDiagramElementId() {
        return diagramElementId;
    }

    public String getDescription() {
        return description;
    }

    public String getEndpointBaseUri() {
        return endpointBaseUri;
    }
}
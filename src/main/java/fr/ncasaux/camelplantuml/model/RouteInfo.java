package fr.ncasaux.camelplantuml.model;

public class RouteInfo {
    private final String endpointBaseUri;
    private final String description;
    private String diagramElementId;

    public RouteInfo(String endpointBaseUri, String description) {
        this.description = description;
        this.endpointBaseUri = endpointBaseUri;
    }

    @Override
    public String toString() {
        return "Route consuming from endpointBaseUri \"".concat(endpointBaseUri).concat("\"")
                .concat(" with description \"").concat(description).concat("\"");
    }

    public String getDescription() {
        return description;
    }

    public String getDiagramElementId() {
        return diagramElementId;
    }

    public void setDiagramElementId(String diagramElementId) {
        this.diagramElementId = diagramElementId;
    }
}
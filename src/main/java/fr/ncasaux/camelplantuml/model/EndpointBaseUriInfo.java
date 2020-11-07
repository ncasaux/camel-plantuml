package fr.ncasaux.camelplantuml.model;

public class EndpointBaseUriInfo {
    private final String diagramElementId;

    public EndpointBaseUriInfo(String diagramElementId) {
        this.diagramElementId = diagramElementId;
    }

    public String getDiagramElementId() {
        return diagramElementId;
    }
}

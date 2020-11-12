package fr.ncasaux.camelplantuml.model;

public class EndpointBaseUriInfo {
    private String diagramElementId;

    public EndpointBaseUriInfo() {
    }

    public EndpointBaseUriInfo(String diagramElementId) {
        this.diagramElementId = diagramElementId;
    }

    public void setDiagramElementId(String diagramElementId) {
        this.diagramElementId = diagramElementId;
    }

    public String getDiagramElementId() {
        return diagramElementId;
    }
}

package fr.ncasaux.camelplantuml.model;

public class RouteInfo {
    private String description;
    private String diagramElementId;

    @Override
    public String toString() {
        return "Route with description \"".concat(description).concat("\"");
    }

    public String getDiagramElementId() {
        return diagramElementId;
    }

    public void setDiagramElementId(String diagramElementId) {
        this.diagramElementId = diagramElementId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
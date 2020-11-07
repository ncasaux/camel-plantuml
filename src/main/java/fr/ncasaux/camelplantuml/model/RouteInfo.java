package fr.ncasaux.camelplantuml.model;

public class RouteInfo {
    private String description;
    private String diagramElementId;

    public RouteInfo(String description, String diagramElementId) {
        this.description = description;
        this.diagramElementId = diagramElementId;
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
}
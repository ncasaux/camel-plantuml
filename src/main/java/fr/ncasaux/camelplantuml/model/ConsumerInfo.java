package fr.ncasaux.camelplantuml.model;

public class ConsumerInfo {
    private String routeId;
    private String endpointUri;
    private String processorType;
    private Boolean useDynamicEndpoint;

    @Override
    public boolean equals(Object obj) {
        ConsumerInfo ci = (ConsumerInfo) obj;
        return obj.getClass().equals(ConsumerInfo.class)
                && this.endpointUri.equals(ci.endpointUri)
                && this.routeId.equals(ci.routeId)
                && this.processorType.equals(ci.processorType)
                && this.useDynamicEndpoint.equals(ci.useDynamicEndpoint);
    }

    @Override
    public String toString() {
        return "Consumer in routeId \"".concat(routeId).concat("\" ")
                .concat("consuming from ").concat(useDynamicEndpoint ? "dynamic " : "static ")
                .concat("URI \"").concat(endpointUri).concat("\" ")
                .concat("through processor \"").concat(processorType).concat("\" ");
    }

    public Boolean getUseDynamicEndpoint() {
        return useDynamicEndpoint;
    }

    public void setUseDynamicEndpoint(Boolean useDynamicEndpoint) {
        this.useDynamicEndpoint = useDynamicEndpoint;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }
}
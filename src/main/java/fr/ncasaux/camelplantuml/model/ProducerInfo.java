package fr.ncasaux.camelplantuml.model;

public class ProducerInfo {
    private String routeId;
    private String endpointUri;
    private String processorType;
    private Boolean useDynamicEndpoint;

    @Override
    public boolean equals(Object obj) {
        ProducerInfo pi = (ProducerInfo) obj;
        return obj.getClass().equals(ProducerInfo.class)
                && this.endpointUri.equals(pi.endpointUri)
                && this.routeId.equals(pi.routeId)
                && this.processorType.equals(pi.processorType)
                && this.useDynamicEndpoint.equals(pi.useDynamicEndpoint);
    }

    @Override
    public String toString() {
        return "Producer in routeId \"".concat(routeId).concat("\" ")
                .concat("producing to ").concat(useDynamicEndpoint ? "dynamic " : "static ")
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
package io.github.ncasaux.camelplantuml.model;

public class ProducerInfo {
    private final String routeId;
    private final String endpointUri;
    private final String processorType;
    private final Boolean useDynamicEndpoint;

    public ProducerInfo(String routeId, String endpointUri, String processorType, Boolean useDynamicEndpoint) {
        this.routeId = routeId;
        this.endpointUri = endpointUri;
        this.processorType = processorType;
        this.useDynamicEndpoint = useDynamicEndpoint;
    }

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
                .concat("through processor \"").concat(processorType).concat("\"");
    }

    public Boolean getUseDynamicEndpoint() {
        return useDynamicEndpoint;
    }

    public String getProcessorType() {
        return processorType;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getEndpointUri() {
        return endpointUri;
    }
}
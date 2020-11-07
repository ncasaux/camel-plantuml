package fr.ncasaux.camelplantuml.model;

public class ConsumerInfo {
    private final String routeId;
    private final String endpointUri;
    private final String processorType;
    private final Boolean useDynamicEndpoint;

    public ConsumerInfo(String routeId, String endpointUri, String processorType, Boolean useDynamicEndpoint) {
        this.routeId = routeId;
        this.endpointUri = endpointUri;
        this.processorType = processorType;
        this.useDynamicEndpoint = useDynamicEndpoint;
    }

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
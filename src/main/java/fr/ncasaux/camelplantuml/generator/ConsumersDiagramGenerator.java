package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.routeIdFilters;

public class ConsumersDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumersDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           HashMap<String, EndpointUriInfo> endpointUrisInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           HashMap<String, RouteInfo> routesInfo) throws IOException {

        String umlConsumerTemplate = IOUtils.toString(Objects.requireNonNull(ConsumersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/consumerTemplate")),StandardCharsets.UTF_8);
        String umlDynamicConsumerRouteTemplate = IOUtils.toString(Objects.requireNonNull(ConsumersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/dynamicConsumerTemplate")),StandardCharsets.UTF_8);
        String umlString = "";

        for (int index = 0; index < consumersInfo.size(); index++) {

            ConsumerInfo consumerInfo = consumersInfo.get(index);

            String routeId = consumerInfo.getRouteId();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();
            String processorType = consumerInfo.getProcessorType();

            boolean drawRoute = true;

            for (String filter : routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawRoute = false;
                    LOGGER.info("Consumer \"{}\" matches the routeId filter \"{}\", it will not be part of the diagram", consumerInfo, filter);
                }
            }

            if (drawRoute) {

                if (!consumerInfo.getUseDynamicEndpoint()) {
                    String endpointBaseUri = endpointUrisInfo.get(consumerInfo.getEndpointUri()).getEndpointBaseUri();
                    String endpointElementId = endpointBaseUrisInfo.get(endpointBaseUri).getDiagramElementId();

                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlConsumerTemplate,
                                    new String[]{"%%endpointElementId%%", "%%routeElementId%%", "%%processorType%%"},
                                    new String[]{endpointElementId, routeElementId, processorType}))
                            .concat("\n\n");
                } else {
                    String uri = consumerInfo.getEndpointUri();
                    String endpointElementId = "dynamic_consumer_endpoint_".concat(String.valueOf(index));

                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlDynamicConsumerRouteTemplate,
                                    new String[]{"%%endpointElementId%%", "%%uri%%", "%%routeElementId%%", "%%processorType%%"},
                                    new String[]{endpointElementId, uri, routeElementId, processorType}))
                            .concat("\n\n")
                    ;
                }
            }
        }
        return umlString;
    }
}

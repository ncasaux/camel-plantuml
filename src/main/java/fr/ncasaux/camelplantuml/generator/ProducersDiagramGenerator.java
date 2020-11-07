package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
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

public class ProducersDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducersDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointUriInfo> endpointUrisInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           HashMap<String, RouteInfo> routesInfo) throws IOException {

        String umlProducerTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/producerTemplate")),StandardCharsets.UTF_8);
        String umlDynamicProducerRouteTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/dynamicProducerTemplate")),StandardCharsets.UTF_8);
        String umlString = "";

        for (int index = 0; index < producersInfo.size(); index++) {

            ProducerInfo producerInfo = producersInfo.get(index);

            String routeId = producerInfo.getRouteId();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();
            String processorType = producerInfo.getProcessorType();

            boolean drawRoute = true;

            for (String filter : routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawRoute = false;
                    LOGGER.info("Producer \"{}\" matches the routeId filter \"{}\", it will not be part of the diagram", producerInfo, filter);
                }
            }

            if (drawRoute) {
                if (!producerInfo.getUseDynamicEndpoint()) {
                    try {
                        String endpointBaseUri = endpointUrisInfo.get(producerInfo.getEndpointUri()).getEndpointBaseUri();
                        String endpointElementId = endpointBaseUrisInfo.get(endpointBaseUri).getDiagramElementId();

                        umlString = umlString
                                .concat(StringUtils.replaceEach(umlProducerTemplate,
                                        new String[]{"%%endpointElementId%%", "%%routeElementId%%", "%%processorType%%"},
                                        new String[]{endpointElementId, routeElementId, processorType}))
                                .concat("\n\n");
                    } catch (Exception e) {
                        LOGGER.warn("Could not find endpointBaseUri for endpoint \"{}\"", producerInfo.getEndpointUri());
                    }
                } else {
                    String uri = producerInfo.getEndpointUri();
                    String endpointElementId = "dynamic_producer_endpoint_".concat(String.valueOf(index));

                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlDynamicProducerRouteTemplate,
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

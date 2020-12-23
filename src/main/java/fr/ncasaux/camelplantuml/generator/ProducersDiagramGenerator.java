package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import fr.ncasaux.camelplantuml.model.query.Parameters;
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

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           HashMap<String, RouteInfo> routesInfo,
                                           Parameters parameters) throws IOException {

        String umlProducerTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/producerTemplate")), StandardCharsets.UTF_8);
        String umlDynamicProducerRouteTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/dynamicProducerTemplate")), StandardCharsets.UTF_8);
        String umlString = "";

        for (int index = 0; index < producersInfo.size(); index++) {

            ProducerInfo producerInfo = producersInfo.get(index);

            String routeId = producerInfo.getRouteId();
            String processorType = producerInfo.getProcessorType();
            String endpointBaseUri = producerInfo.getEndpointUri();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();

            boolean drawProducer = true;

            for (String filter : routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawProducer = false;
                    LOGGER.info("\"{}\" matches the routeId filter \"{}\", producer \"{}\" will not be part of the diagram", routeId, filter, producerInfo);
                    break;
                }
            }

            if (drawProducer) {
                if (!producerInfo.getUseDynamicEndpoint()) {
                    String targetElementId = endpointBaseUrisInfo.get(endpointBaseUri).getDiagramElementId();
                    if (parameters.connectRoutes()) {
                        ConsumerInfo ci = consumersInfo.stream().filter(consumerInfo -> consumerInfo.getEndpointUri().equals(endpointBaseUri)).findFirst().orElse(null);
                        if (ci != null) {
                            targetElementId = routesInfo.get(ci.getRouteId()).getDiagramElementId();
                            LOGGER.info("Parameter \"connectRoutes\" is \"true\", producer in routeId \"{}\" will be directly connected to routeId \"{}\", bypassing endpointBaseUri \"{}\"", routeId, ci.getRouteId(), endpointBaseUri);
                        }
                    }

                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlProducerTemplate,
                                    new String[]{"%%targetElementId%%", "%%routeElementId%%", "%%processorType%%"},
                                    new String[]{targetElementId, routeElementId, processorType}))
                            .concat("\n\n");

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

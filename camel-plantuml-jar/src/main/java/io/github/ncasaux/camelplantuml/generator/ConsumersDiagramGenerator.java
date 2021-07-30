package io.github.ncasaux.camelplantuml.generator;

import io.github.ncasaux.camelplantuml.model.ConsumerInfo;
import io.github.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import io.github.ncasaux.camelplantuml.model.ProducerInfo;
import io.github.ncasaux.camelplantuml.model.RouteInfo;
import io.github.ncasaux.camelplantuml.model.query.Parameters;
import io.github.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ConsumersDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumersDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           HashMap<String, RouteInfo> routesInfo,
                                           Parameters parameters) throws IOException, URISyntaxException {

        String umlConsumerTemplate = IOUtils.toString(Objects.requireNonNull(ConsumersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/consumerTemplate")), StandardCharsets.UTF_8);
        String umlDynamicConsumerRouteTemplate = IOUtils.toString(Objects.requireNonNull(ConsumersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/dynamicConsumerTemplate")), StandardCharsets.UTF_8);
        String umlString = "";

        for (int index = 0; index < consumersInfo.size(); index++) {
            ConsumerInfo consumerInfo = consumersInfo.get(index);

            String routeId = consumerInfo.getRouteId();
            String processorType = consumerInfo.getProcessorType();
            String endpointBaseUri = consumerInfo.getEndpointUri();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();

            boolean drawConsumer = true;

            for (String filter : GetRoutesInfoProcessor.routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawConsumer = false;
                    LOGGER.info("{} matches the routeId filter \"{}\", consumer will not be part of the diagram", consumerInfo, filter);
                    break;
                }
            }

            if (parameters.connectRoutes() && producersInfo.stream().anyMatch(producerInfo -> producerInfo.getEndpointUri().equals(endpointBaseUri)) && Arrays.asList(GetRoutesInfoProcessor.camelInternalEndpointSchemeFilters).contains(new URI(endpointBaseUri).getScheme())) {
                drawConsumer = false;
                LOGGER.info("Parameter \"connectRoutes\" is \"true\", consumer in routeId \"{}\" from internal endpointBaseUri \"{}\" will not be part of the diagram", routeId, endpointBaseUri);
            }

            if (drawConsumer) {
                if (!consumerInfo.getUseDynamicEndpoint()) {
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

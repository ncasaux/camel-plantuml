package io.github.ncasaux.camelplantuml.generator;

import io.github.ncasaux.camelplantuml.model.ConsumerInfo;
import io.github.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import io.github.ncasaux.camelplantuml.model.ProducerInfo;
import io.github.ncasaux.camelplantuml.model.RouteInfo;
import io.github.ncasaux.camelplantuml.model.query.Parameters;
import io.github.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;
import org.apache.commons.codec.digest.DigestUtils;
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

        for (ConsumerInfo consumerInfo : consumersInfo) {
            String routeId = consumerInfo.getRouteId();
            String processorType = consumerInfo.getProcessorType();
            String endpointBaseUri = consumerInfo.getEndpointUri();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();
            String routeEndpointBaseUri = routesInfo.get(routeId).getEndpointBaseUri();

            boolean drawConsumer = true;

            for (String routeIdFilter : GetRoutesInfoProcessor.routeIdFilters) {
                if (routeId.matches(routeIdFilter)) {
                    drawConsumer = false;
                    LOGGER.info("{} matches the routeId filter \"{}\", consumer will not be part of the diagram", consumerInfo, routeIdFilter);
                    break;
                }
            }

            for (String endpointBaseUriFilter : GetRoutesInfoProcessor.endpointBaseUriFilters) {
                if (routeEndpointBaseUri.matches(endpointBaseUriFilter)) {
                    drawConsumer = false;
                    LOGGER.info("{} matches the endpoint filter \"{}\", consumer will not be part of the diagram", consumerInfo, endpointBaseUriFilter);
                    break;
                }
            }

            if (parameters.uriFilterPattern().matcher(endpointBaseUri).matches()) {
                drawConsumer = false;
                LOGGER.info("{} matches the uriFilterPattern \"{}\", consumer will not be part of the diagram", consumerInfo, parameters.uriFilterPattern());
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
                    String endpointElementId = "dynamic_consumer_endpoint_".concat(DigestUtils.md5Hex(uri));

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

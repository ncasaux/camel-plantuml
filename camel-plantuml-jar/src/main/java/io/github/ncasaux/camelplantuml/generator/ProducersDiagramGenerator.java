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
import java.util.*;

public class ProducersDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducersDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           HashMap<String, RouteInfo> routesInfo,
                                           Parameters parameters) throws IOException, URISyntaxException {

        String umlProducerTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/producerTemplate")), StandardCharsets.UTF_8);
        String umlDynamicProducerRouteTemplate = IOUtils.toString(Objects.requireNonNull(ProducersDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/dynamicProducerTemplate")), StandardCharsets.UTF_8);
        String umlString = "";

        for (ProducerInfo producerInfo : producersInfo) {

            String routeId = producerInfo.getRouteId();
            String processorType = producerInfo.getProcessorType();
            String endpointBaseUri = producerInfo.getEndpointUri();
            String routeElementId = routesInfo.get(routeId).getDiagramElementId();

            boolean drawProducer = true;

            for (String filter : GetRoutesInfoProcessor.routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawProducer = false;
                    LOGGER.info("{} matches the routeId filter \"{}\", producer will not be part of the diagram", producerInfo, filter);
                    break;
                }
            }

            if (drawProducer) {
                if (!producerInfo.getUseDynamicEndpoint()) {
                    String targetElementId = endpointBaseUrisInfo.get(endpointBaseUri).getDiagramElementId();
                    if (parameters.connectRoutes() && Arrays.asList(GetRoutesInfoProcessor.camelInternalEndpointSchemeFilters).contains(new URI(endpointBaseUri).getScheme())) {
                        ConsumerInfo ci = consumersInfo.stream().filter(consumerInfo -> consumerInfo.getEndpointUri().equals(endpointBaseUri)).findFirst().orElse(null);
                        if (ci != null) {
                            processorType = processorType.concat(" / ").concat(ci.getProcessorType());
                            targetElementId = routesInfo.get(ci.getRouteId()).getDiagramElementId();
                            LOGGER.info("Parameter \"connectRoutes\" is \"true\", producer in routeId \"{}\" will be directly connected to routeId \"{}\", bypassing internal endpointBaseUri \"{}\"", routeId, ci.getRouteId(), endpointBaseUri);
                        }
                    }

                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlProducerTemplate,
                                    new String[]{"%%targetElementId%%", "%%routeElementId%%", "%%processorType%%"},
                                    new String[]{targetElementId, routeElementId, processorType}))
                            .concat("\n\n");

                } else {
                    String uri = producerInfo.getEndpointUri();
//                    String endpointElementId = "dynamic_producer_endpoint_".concat(String.valueOf(index));
                    String endpointElementId = "dynamic_producer_endpoint_".concat(DigestUtils.md5Hex(uri));

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

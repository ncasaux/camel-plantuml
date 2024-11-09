package io.github.ncasaux.camelplantuml.generator;

import io.github.ncasaux.camelplantuml.model.ConsumerInfo;
import io.github.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import io.github.ncasaux.camelplantuml.model.ProducerInfo;
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
import java.util.*;

public class EndpointsDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           Parameters parameters) throws IOException, URISyntaxException {

        String umlEndpointTemplate = IOUtils.toString(Objects.requireNonNull(EndpointsDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/endpointTemplate")), StandardCharsets.UTF_8);
        String umlString = "";

        for (Map.Entry<String, EndpointBaseUriInfo> endpointBaseUriInfoEntry : endpointBaseUrisInfo.entrySet()) {
            String endpointBaseUri = endpointBaseUriInfoEntry.getKey();
            String diagramElementId = endpointBaseUriInfoEntry.getValue().getDiagramElementId();

            boolean endpointHasConsumer = consumersInfo.stream().anyMatch(consumerInfo -> consumerInfo.getEndpointUri().equals(endpointBaseUri));
            boolean endpointHasProducer = producersInfo.stream().anyMatch(producerInfo -> producerInfo.getEndpointUri().equals(endpointBaseUri));
            boolean drawEndpoint = true;

            for (String filter : GetRoutesInfoProcessor.endpointBaseUriFilters) {
                if (endpointBaseUri.matches(filter)) {
                    drawEndpoint = false;
                    LOGGER.info("EndpointBaseUri \"{}\" matches the endpoint filter \"{}\", endpoint will not be part of the diagram", endpointBaseUri, filter);
                    break;
                }
            }

            if (parameters.uriFilterPattern().matcher(endpointBaseUri).matches()) {
                drawEndpoint = false;
                LOGGER.info("EndpointBaseUri \"{}\" matches the uriFilterPattern \"{}\", endpoint will not be part of the diagram", endpointBaseUri, parameters.uriFilterPattern());
            }

            if (parameters.connectRoutes() && endpointHasConsumer && endpointHasProducer && Arrays.asList(GetRoutesInfoProcessor.camelInternalEndpointSchemeFilters).contains(new URI(endpointBaseUri).getScheme())) {
                drawEndpoint = false;
                LOGGER.info("Parameter \"connectRoutes\" is \"true\", endpointBaseUri \"{}\" is internal and has both consumer and producer, endpoint will not be part of the diagram", endpointBaseUri);
            }

            if (drawEndpoint) {
                umlString = umlString
                        .concat(StringUtils.replaceEach(umlEndpointTemplate,
                                new String[]{"%%endpointBaseUri%%", "%%endpointElementId%%"},
                                new String[]{endpointBaseUri, diagramElementId}))
                        .concat("\n\n")
                ;
            }
        }
        return umlString;
    }
}
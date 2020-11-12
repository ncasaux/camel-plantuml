package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.endpointBaseUriFilters;

public class EndpointsDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsDiagramGenerator.class);

    public static String generateUmlString(ArrayList<ConsumerInfo> consumersInfo,
                                           ArrayList<ProducerInfo> producersInfo,
                                           HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo,
                                           boolean connectRoutes) throws IOException {

        String umlEndpointTemplate = IOUtils.toString(Objects.requireNonNull(EndpointsDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/endpointTemplate")), StandardCharsets.UTF_8);
        String umlString = "";

        for (Map.Entry<String, EndpointBaseUriInfo> endpointBaseUriInfoEntry : endpointBaseUrisInfo.entrySet()) {

            String endpointBaseUri = endpointBaseUriInfoEntry.getKey();
            String diagramElementId = endpointBaseUriInfoEntry.getValue().getDiagramElementId();

            boolean drawEndpoint = true;

            for (String filter : endpointBaseUriFilters) {
                if (endpointBaseUri.matches(filter)) {
                    drawEndpoint = false;
                    LOGGER.info("EndpointBaseUri \"{}\" matches the endpoint filter \"{}\", it will not be part of the diagram", endpointBaseUri, filter);
                    break;
                }
            }

            boolean endpointHasConsumer = consumersInfo.stream().anyMatch(consumerInfo -> consumerInfo.getEndpointUri().equals(endpointBaseUri));
            boolean endpointHasProducer = producersInfo.stream().anyMatch(producerInfo -> producerInfo.getEndpointUri().equals(endpointBaseUri));

            if (connectRoutes && endpointHasConsumer && endpointHasProducer) {
                drawEndpoint = false;
                LOGGER.info("Parameter \"connectRoutes\" is \"true\", and endpointBaseUri \"{}\" has both consumer and producer, it will not be part of the diagram", endpointBaseUri);
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
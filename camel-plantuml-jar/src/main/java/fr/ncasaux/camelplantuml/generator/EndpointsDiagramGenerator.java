package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.model.query.Parameters;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.camelInternalEndpointSchemeFilters;
import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.endpointBaseUriFilters;

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

            for (String filter : endpointBaseUriFilters) {
                if (endpointBaseUri.matches(filter)) {
                    drawEndpoint = false;
                    LOGGER.info("EndpointBaseUri \"{}\" matches the endpoint filter \"{}\", endpoint will not be part of the diagram", endpointBaseUri, filter);
                    break;
                }
            }

            if (parameters.connectRoutes() && endpointHasConsumer && endpointHasProducer && Arrays.asList(camelInternalEndpointSchemeFilters).contains(new URI(endpointBaseUri).getScheme())) {
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
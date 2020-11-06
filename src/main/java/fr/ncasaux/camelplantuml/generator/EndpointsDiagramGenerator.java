package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.endpointBaseUriFilters;

public class EndpointsDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsDiagramGenerator.class);

    public static String generateUmlString(HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo) throws URISyntaxException, IOException {

        String umlEndpointTemplate = Files.readString(Paths.get(ClassLoader.getSystemResource("plantuml/endpointTemplate").toURI()), StandardCharsets.UTF_8);
        String umlString = "";

        for (Map.Entry<String, EndpointBaseUriInfo> endpointInfoEntry : endpointBaseUrisInfo.entrySet()) {

            String endpointBaseUri = endpointInfoEntry.getKey();
            String diagramElementId = endpointInfoEntry.getValue().getDiagramElementId();

            boolean drawEndpoint = true;

            for (String filter : endpointBaseUriFilters) {
                if (endpointBaseUri.matches(filter)) {
                    drawEndpoint = false;
                    LOGGER.info("EndpointBaseUri \"{}\" matches the endpoint filter \"{}\", it will not be part of the diagram", endpointBaseUri, filter);
                }
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
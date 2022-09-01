package io.github.ncasaux.camelplantuml.generator;

import io.github.ncasaux.camelplantuml.model.RouteInfo;
import io.github.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RoutesDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesDiagramGenerator.class);

    public static String generateUmlString(HashMap<String, RouteInfo> routesInfo) throws IOException {

        String umlRouteTemplate = IOUtils.toString(Objects.requireNonNull(RoutesDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/routeTemplate")), StandardCharsets.UTF_8);
        String umlRouteTemplateNoDescription = IOUtils.toString(Objects.requireNonNull(RoutesDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/routeTemplateNoDescription")), StandardCharsets.UTF_8);
        String umlString = "";

        for (Map.Entry<String, RouteInfo> routeInfoEntry : routesInfo.entrySet()) {
            String routeId = routeInfoEntry.getKey();
            String diagramElementId = routeInfoEntry.getValue().getDiagramElementId();
            String routeDescription = routeInfoEntry.getValue().getDescription();
            String routeEndpointBaseUri = routeInfoEntry.getValue().getEndpointBaseUri();

            boolean drawRoute = true;

            for (String routeIdFilter : GetRoutesInfoProcessor.routeIdFilters) {
                if (routeId.matches(routeIdFilter)) {
                    drawRoute = false;
                    LOGGER.info("Route with id \"{}\" matches the routeId filter \"{}\", route will not be part of the diagram", routeId, routeIdFilter);
                    break;
                }
            }

            for (String endpointBaseUriFilter : GetRoutesInfoProcessor.endpointBaseUriFilters) {
                if (routeEndpointBaseUri.matches(endpointBaseUriFilter)) {
                    drawRoute = false;
                    LOGGER.info("Route with id \"{}\" matches the endpoint filter \"{}\", route will not be part of the diagram", routeId, endpointBaseUriFilter);
                    break;
                }
            }

            if (drawRoute) {
                if (routeDescription == null || routeDescription.isEmpty()) {
                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlRouteTemplateNoDescription,
                                    new String[]{"%%routeId%%", "%%routeElementId%%", "%%routeDescription%%"},
                                    new String[]{routeId, diagramElementId, routeDescription}))
                            .concat("\n\n");
                } else {
                    umlString = umlString
                            .concat(StringUtils.replaceEach(umlRouteTemplate,
                                    new String[]{"%%routeId%%", "%%routeElementId%%", "%%routeDescription%%"},
                                    new String[]{routeId, diagramElementId, routeDescription}))
                            .concat("\n\n");
                }
            }
        }
        return umlString;
    }
}


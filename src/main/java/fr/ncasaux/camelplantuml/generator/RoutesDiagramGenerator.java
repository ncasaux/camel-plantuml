package fr.ncasaux.camelplantuml.generator;

import fr.ncasaux.camelplantuml.model.RouteInfo;
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

import static fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor.routeIdFilters;

public class RoutesDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesDiagramGenerator.class);

    public static String generateUmlString(HashMap<String, RouteInfo> routesInfo) throws URISyntaxException, IOException {

        String umlRouteTemplate = Files.readString(Paths.get(ClassLoader.getSystemResource("plantuml/routeTemplate").toURI()), StandardCharsets.UTF_8);
        String umlString = "";

        for (Map.Entry<String, RouteInfo> routeInfoEntry : routesInfo.entrySet()) {
            String routeId = routeInfoEntry.getKey();
            String diagramElementId = routeInfoEntry.getValue().getDiagramElementId();
            String routeDescription = routeInfoEntry.getValue().getDescription();

            boolean drawRoute = true;

            for (String filter : routeIdFilters) {
                if (routeId.matches(filter)) {
                    drawRoute = false;
                    LOGGER.info("Route with id \"{}\" matches the routeId filter \"{}\", it will not be part of the diagram", routeId, filter);
                }
            }

            if (drawRoute) {
                umlString = umlString
                        .concat(StringUtils.replaceEach(umlRouteTemplate,
                                new String[]{"%%routeId%%", "%%routeElementId%%", "%%routeDescription%%"},
                                new String[]{routeId, diagramElementId, routeDescription}))
                        .concat("\n\n");
            }
        }
        return umlString;
    }
}


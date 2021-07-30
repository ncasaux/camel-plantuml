package io.github.ncasaux.camelplantuml.utils;

import io.github.ncasaux.camelplantuml.model.RouteInfo;
import org.slf4j.Logger;

import java.util.HashMap;

public class RouteUtils {

    public static void addRouteInfo(HashMap<String, RouteInfo> routesInfo,
                                    String routeId,
                                    RouteInfo routeInfo,
                                    Logger LOGGER) {

        routeInfo.setDiagramElementId("route_".concat(String.valueOf(routesInfo.size())));
        routesInfo.put(routeId, routeInfo);
        LOGGER.info("{} and routeId \"{}\" added to the map of routes", routeInfo, routeId);
    }
}

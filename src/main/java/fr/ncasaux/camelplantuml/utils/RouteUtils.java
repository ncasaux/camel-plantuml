package fr.ncasaux.camelplantuml.utils;

import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.slf4j.Logger;

import java.util.HashMap;

public class RouteUtils {

    public static void addRouteInfo(HashMap<String, RouteInfo> routesInfo, String routeId, RouteInfo routeInfo, Logger LOGGER) {
        routesInfo.put(routeId, routeInfo);
        LOGGER.info("{} and id \"{}\" added to the map of routes", routeInfo.toString(), routeId);
    }
}

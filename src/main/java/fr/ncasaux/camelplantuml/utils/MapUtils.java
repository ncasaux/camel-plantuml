package fr.ncasaux.camelplantuml.utils;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.slf4j.Logger;

import java.util.HashMap;

public class MapUtils {
    public static void addEndpointUriInfo(HashMap<String, EndpointUriInfo> endpointUrisInfo, String endpointUri, EndpointUriInfo endpointUriInfo, Logger LOGGER) {
        endpointUrisInfo.put(endpointUri, endpointUriInfo);
        LOGGER.info("{} with id \"{}\" added to the map of endpointUris", endpointUriInfo.toString(), endpointUri);
    }

    public static void addEndpointBaseUriInfo(HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo, String endpointBaseUri, EndpointBaseUriInfo endpointBaseUriInfo, Logger LOGGER) {
        endpointBaseUrisInfo.put(endpointBaseUri, endpointBaseUriInfo);
        LOGGER.info("EndpointBaseUri with id \"{}\" added to the map of endpointBaseUris", endpointBaseUri);
    }

    public static void addRouteInfo(HashMap<String, RouteInfo> routesInfo, String routeId, RouteInfo routeInfo, Logger LOGGER) {
        routesInfo.put(routeId, routeInfo);
        LOGGER.info("{} and id \"{}\" added to the map of routes", routeInfo.toString(), routeId);
    }
}

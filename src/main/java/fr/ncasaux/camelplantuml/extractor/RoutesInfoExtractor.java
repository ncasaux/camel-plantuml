package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import fr.ncasaux.camelplantuml.utils.ConsumerUtils;
import fr.ncasaux.camelplantuml.utils.EndpointUtils;
import fr.ncasaux.camelplantuml.utils.RouteUtils;
import org.apache.camel.util.URISupport;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoutesInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getRoutesInfo(MBeanServer mbeanServer,
                                     HashMap<String, RouteInfo> routesInfo,
                                     ArrayList<ConsumerInfo> consumersInfo,
                                     HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo) throws Exception {

        Set<ObjectName> routesSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=routes,*"), null);
        List<ObjectName> routesList = new ArrayList<>();
        CollectionUtils.addAll(routesList, routesSet);

        for (ObjectName on : routesList) {
            String routeState = (String) mbeanServer.getAttribute(on, "State");
            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");

            LOGGER.debug("Processing routeId \"{}\"", routeId);
            if (!routeState.equalsIgnoreCase("Started")) {
                LOGGER.warn("Route with id \"{}\" is not started, associated processors may not have been created, diagram may be incomplete", routeId);
            }

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");
            String normalizedUri = URISupport.normalizeUri(endpointUri);
            String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

            String actualDescription = (String) mbeanServer.getAttribute(on, "Description");
            String description = actualDescription != null ? actualDescription : "No description...";

            RouteInfo routeInfo = new RouteInfo(endpointBaseUri, description);
            RouteUtils.addRouteInfo(routesInfo, routeId, routeInfo, LOGGER);

            ConsumerInfo consumerInfo = new ConsumerInfo(routeId, endpointBaseUri, "from", false);
            ConsumerUtils.addConsumerInfoIfNotInList(consumersInfo, consumerInfo, LOGGER);

            EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
            EndpointUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
        }
    }
}

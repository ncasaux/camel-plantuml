package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoutesInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getRoutesInfo(MBeanServer mbeanServer, HashMap<String, RouteInfo> routesInfo) throws Exception {

        Set<ObjectName> routesSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=routes,*"), null);
        List<ObjectName> routesList = List.copyOf(routesSet);

        for (int index = 0; index < routesList.size(); index++) {
            ObjectName on = routesList.get(index);
            RouteInfo routeInfo = new RouteInfo();

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");

            String description = (String) mbeanServer.getAttribute(on, "Description");
            routeInfo.setDescription(description != null ? description : "No description...");

            routeInfo.setDiagramElementId("route_".concat(String.valueOf(index)));

            routesInfo.put(routeId, routeInfo);
            LOGGER.info("{} and id \"{}\" added to the map of routes", routeInfo.toString(), routeId);
        }
    }
}

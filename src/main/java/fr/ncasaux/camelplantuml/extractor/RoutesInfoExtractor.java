package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.RouteInfo;
import fr.ncasaux.camelplantuml.utils.MapUtils;
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

            String actualDescription = (String) mbeanServer.getAttribute(on, "Description");
            String description = actualDescription != null ? actualDescription : "No description...";
            RouteInfo routeInfo = new RouteInfo(description, "route_".concat(String.valueOf(index)));

            MapUtils.addRouteInfo(routesInfo, (String) mbeanServer.getAttribute(on, "RouteId"), routeInfo, LOGGER);
        }
    }
}

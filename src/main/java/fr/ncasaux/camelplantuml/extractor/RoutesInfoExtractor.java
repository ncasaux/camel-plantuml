package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import fr.ncasaux.camelplantuml.utils.MapUtils;
import org.apache.camel.support.EndpointHelper;
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

import static fr.ncasaux.camelplantuml.utils.EndpointUtils.getEndpointBaseUri;
import static fr.ncasaux.camelplantuml.utils.ListUtils.addConsumerInfoIfNotInList;

public class RoutesInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getRoutesInfo(MBeanServer mbeanServer,
                                     HashMap<String, RouteInfo> routesInfo,
                                     ArrayList<ConsumerInfo> consumersInfo) throws Exception {

        Set<ObjectName> routesSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=routes,*"), null);
        List<ObjectName> routesList = new ArrayList<>();
        CollectionUtils.addAll(routesList, routesSet);

        for (int index = 0; index < routesList.size(); index++) {
            ObjectName on = routesList.get(index);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");
            String normalizedUri = EndpointHelper.normalizeEndpointUri(endpointUri);
            String endpointBaseUri = URLDecoder.decode(getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

            String actualDescription = (String) mbeanServer.getAttribute(on, "Description");
            String description = actualDescription != null ? actualDescription : "No description...";

            RouteInfo routeInfo = new RouteInfo(description, "route_".concat(String.valueOf(index)), endpointBaseUri);
            MapUtils.addRouteInfo(routesInfo, routeId, routeInfo, LOGGER);

            ConsumerInfo consumerInfo = new ConsumerInfo(routeId, endpointBaseUri,"from",false);
            addConsumerInfoIfNotInList(consumersInfo, consumerInfo, LOGGER);
        }
    }
}

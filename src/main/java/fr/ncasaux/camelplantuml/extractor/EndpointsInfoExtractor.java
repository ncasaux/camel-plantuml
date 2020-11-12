package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import org.apache.camel.util.URISupport;
import fr.ncasaux.camelplantuml.utils.EndpointUtils;
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

public class EndpointsInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getEndpointsInfo(MBeanServer mbeanServer,
                                        HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws Exception {

        Set<ObjectName> endpointsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=endpoints,*"), null);
        List<ObjectName> endpointsList = new ArrayList<>();
        CollectionUtils.addAll(endpointsList, endpointsSet);

        for (int index = 0; index < endpointsList.size(); index++) {
            ObjectName on = endpointsList.get(index);

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");

            String normalizedUri = URISupport.normalizeUri(endpointUri);
            String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

            EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_".concat(String.valueOf(index)));
            EndpointUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
        }
    }
}

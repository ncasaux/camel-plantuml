package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EndpointsInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getEndpointsInfo(MBeanServer mbeanServer,
                                        HashMap<String, EndpointUriInfo> endpointUrisInfo,
                                        HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws Exception {

        Set<ObjectName> endpointsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=endpoints,*"), null);
        List<ObjectName> endpointsList = List.copyOf(endpointsSet);

        for (int index = 0; index < endpointsList.size(); index++) {
            ObjectName on = endpointsList.get(index);

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");

            EndpointUriInfo endpointUriInfo = new EndpointUriInfo(endpointUri);
            MapUtils.addEndpointUriInfo(endpointUrisInfo, endpointUri, endpointUriInfo, LOGGER);

            EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_".concat(String.valueOf(index)));
            MapUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointUriInfo.getEndpointBaseUri(), endpointBaseUriInfo, LOGGER);
        }
    }
}

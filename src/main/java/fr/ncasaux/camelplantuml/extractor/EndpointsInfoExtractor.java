package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

            EndpointUriInfo endpointUriInfo = new EndpointUriInfo();
            String endpointUri = URLDecoder.decode((String) mbeanServer.getAttribute(on, "EndpointUri"), StandardCharsets.UTF_8);
            String endpointBaseUri = URLDecoder.decode((String) mbeanServer.getAttribute(on, "EndpointBaseUri"), StandardCharsets.UTF_8);
            endpointUriInfo.setEndpointBaseUri(endpointBaseUri != null ? endpointBaseUri : "");

            endpointUrisInfo.put(endpointUri, endpointUriInfo);
            LOGGER.info("{} with id \"{}\" added to the map of endpointUris", endpointUriInfo.toString(), endpointUri);

            EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
            endpointBaseUriInfo.setDiagramElementId("endpoint_".concat(String.valueOf(index)));
            endpointBaseUrisInfo.put(endpointBaseUri, endpointBaseUriInfo);
            LOGGER.info("EndpointBaseUri with id \"{}\" added to the map of endpointBaseUris", endpointBaseUri);
        }
    }
}

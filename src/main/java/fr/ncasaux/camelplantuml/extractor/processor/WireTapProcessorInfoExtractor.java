package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import fr.ncasaux.camelplantuml.utils.MapUtils;
import org.apache.camel.support.EndpointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WireTapProcessorInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WireTapProcessorInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo,
                                         HashMap<String, EndpointUriInfo> endpointUrisInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedWireTapProcessor"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = List.copyOf(processorsSet);

        for (int index = 0; index < processorsList.size(); index++) {
            ObjectName on = processorsList.get(index);
            String normalizedUri = EndpointHelper.normalizeEndpointUri((String) mbeanServer.getAttribute(on, "Uri"));
            String endpointUri = URLDecoder.decode(normalizedUri, StandardCharsets.UTF_8);

            if ((boolean) mbeanServer.getAttribute(on, "DynamicUri")) {
                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "wiretap", true);
                ListUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);

            } else {
                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "wiretap", false);
                ListUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                EndpointUriInfo endpointUriInfo = new EndpointUriInfo(normalizedUri);
                MapUtils.addEndpointUriInfo(endpointUrisInfo, endpointUri, endpointUriInfo, LOGGER);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_wiretap_".concat(String.valueOf(index)));
                MapUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointUriInfo.getEndpointBaseUri(), endpointBaseUriInfo, LOGGER);
            }
        }
    }
}

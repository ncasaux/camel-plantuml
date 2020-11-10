package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import fr.ncasaux.camelplantuml.utils.MapUtils;
import org.apache.camel.util.URISupport;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static fr.ncasaux.camelplantuml.utils.EndpointUtils.getEndpointBaseUri;

public class WireTapProcessorInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WireTapProcessorInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException, UnsupportedEncodingException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedWireTapProcessor"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = new ArrayList<>();
        CollectionUtils.addAll(processorsList, processorsSet);

        for (int index = 0; index < processorsList.size(); index++) {
            ObjectName on = processorsList.get(index);

            String normalizedUri = URISupport.normalizeUri((String) mbeanServer.getAttribute(on, "Uri"));

            if ((boolean) mbeanServer.getAttribute(on, "DynamicUri")) {
                String endpointUri = URLDecoder.decode(normalizedUri, "UTF-8");

                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "wiretap", true);
                ListUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);
            } else {
                String endpointBaseUri = URLDecoder.decode(getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointBaseUri, "wiretap", false);
                ListUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_wiretap_".concat(String.valueOf(index)));
                MapUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
            }
        }
    }
}

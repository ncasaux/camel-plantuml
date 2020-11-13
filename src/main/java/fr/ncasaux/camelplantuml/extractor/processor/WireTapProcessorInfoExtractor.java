package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.EndpointUtils;
import fr.ncasaux.camelplantuml.utils.ProducerUtils;
import org.apache.camel.support.EndpointHelper;
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

        for (ObjectName on : processorsList) {
            String processorId = (String) mbeanServer.getAttribute(on, "ProcessorId");
            LOGGER.debug("Processing processorId \"{}\"", processorId);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");
            String normalizedUri = EndpointHelper.normalizeEndpointUri((String) mbeanServer.getAttribute(on, "Uri"));

            if ((boolean) mbeanServer.getAttribute(on, "DynamicUri")) {
                String endpointUri = URLDecoder.decode(normalizedUri, "UTF-8");

                ProducerInfo producerInfo = new ProducerInfo(routeId, endpointUri, "wiretap", true);
                ProducerUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);

            } else {
                String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

                ProducerInfo producerInfo = new ProducerInfo(routeId, endpointBaseUri, "wiretap", false);
                ProducerUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
                EndpointUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
            }
        }
    }
}

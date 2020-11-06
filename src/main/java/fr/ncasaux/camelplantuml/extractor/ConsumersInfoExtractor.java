package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Set;

import static fr.ncasaux.camelplantuml.utils.ListUtils.addConsumerInfoIfNotInList;

public class ConsumersInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumersInfoExtractor.class);

    public static void getConsumersInfo(MBeanServer mbeanServer, ArrayList<ConsumerInfo> consumersInfo) throws Exception {

        Set<ObjectName> consumersSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=consumers,*"), null);

        for (ObjectName on : consumersSet) {
            ConsumerInfo consumerInfo = new ConsumerInfo();

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");
            consumerInfo.setRouteId((String) mbeanServer.getAttribute(on, "RouteId"));
            consumerInfo.setEndpointUri(endpointUri != null ? endpointUri : "");
            consumerInfo.setProcessorType("from");
            consumerInfo.setUseDynamicEndpoint(false);

            addConsumerInfoIfNotInList(consumersInfo, consumerInfo);
        }
    }
}
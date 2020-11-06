package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SendDynamicProcessorInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendDynamicProcessorInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedSendDynamicProcessor"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = List.copyOf(processorsSet);

        for (ObjectName on : processorsList) {
            ProducerInfo producerInfo = new ProducerInfo();

            producerInfo.setRouteId((String) mbeanServer.getAttribute(on, "RouteId"));
            producerInfo.setEndpointUri((String) mbeanServer.getAttribute(on, "Uri"));
            producerInfo.setProcessorType("toD");
            producerInfo.setUseDynamicEndpoint(true);

            ListUtils.addProducerInfo(producersInfo, producerInfo);
        }
    }
}

package io.github.ncasaux.camelplantuml.extractor.processor;

import io.github.ncasaux.camelplantuml.model.ProducerInfo;
import io.github.ncasaux.camelplantuml.utils.ProducerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SendDynamicProcessorInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendDynamicProcessorInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServerConnection mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedSendDynamicProcessor"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = new ArrayList<>();
        CollectionUtils.addAll(processorsList, processorsSet);

        for (ObjectName on : processorsList) {
            String processorId = (String) mbeanServer.getAttribute(on, "ProcessorId");
            LOGGER.debug("Processing processorId \"{}\"", processorId);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");

            ProducerInfo producerInfo = new ProducerInfo(routeId, URLDecoder.decode((String) mbeanServer.getAttribute(on, "Uri"), "UTF-8"), "toD", true);
            ProducerUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);
        }
    }
}

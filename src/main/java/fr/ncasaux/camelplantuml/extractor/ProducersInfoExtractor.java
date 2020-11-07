package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.ProducerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Set;

import static fr.ncasaux.camelplantuml.utils.ListUtils.addProducerInfoIfNotInList;

public class ProducersInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducersInfoExtractor.class);

    public static void getProducersInfo(MBeanServer mbeanServer, ArrayList<ProducerInfo> producersInfo) throws Exception {

        Set<ObjectName> producersSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=producers,*"), null);

        for (ObjectName on : producersSet) {
            ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                    (String) mbeanServer.getAttribute(on, "EndpointUri"), "to", false);

            addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);
        }
    }
}
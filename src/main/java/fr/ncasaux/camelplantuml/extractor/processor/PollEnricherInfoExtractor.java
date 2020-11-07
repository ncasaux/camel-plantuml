package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import org.apache.camel.support.EndpointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PollEnricherInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollEnricherInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ConsumerInfo> consumersInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedPollEnricher"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = List.copyOf(processorsSet);

        for (ObjectName on : processorsList) {
            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String endpointUri = URLDecoder.decode(EndpointHelper.normalizeEndpointUri(expression), StandardCharsets.UTF_8);

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                ConsumerInfo consumerInfo = new ConsumerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "pollEnrich", false);
                ListUtils.addConsumerInfoIfNotInList(consumersInfo, consumerInfo, LOGGER);

            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                ConsumerInfo consumerInfo = new ConsumerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "pollEnrich", true);
                ListUtils.addConsumerInfo(consumersInfo, consumerInfo, LOGGER);

            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

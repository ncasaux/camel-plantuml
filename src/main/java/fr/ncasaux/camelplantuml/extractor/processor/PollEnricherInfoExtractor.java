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
            ConsumerInfo consumerInfo = new ConsumerInfo();

            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String normalizedEndpointUri = EndpointHelper.normalizeEndpointUri(expression);
            String endpointUri = URLDecoder.decode(normalizedEndpointUri, StandardCharsets.UTF_8);

            consumerInfo.setRouteId((String) mbeanServer.getAttribute(on, "RouteId"));
            consumerInfo.setEndpointUri(endpointUri);
            consumerInfo.setProcessorType("pollEnrich");

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                consumerInfo.setUseDynamicEndpoint(false);
                ListUtils.addConsumerInfoIfNotInList(consumersInfo, consumerInfo);
            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                consumerInfo.setUseDynamicEndpoint(true);
                ListUtils.addConsumerInfo(consumersInfo, consumerInfo);
            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

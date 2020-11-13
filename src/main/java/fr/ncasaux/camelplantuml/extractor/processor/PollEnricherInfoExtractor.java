package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.utils.ConsumerUtils;
import fr.ncasaux.camelplantuml.utils.EndpointUtils;
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

public class PollEnricherInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollEnricherInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ConsumerInfo> consumersInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException, UnsupportedEncodingException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedPollEnricher"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = new ArrayList<>();

        CollectionUtils.addAll(processorsList, processorsSet);

        for (ObjectName on : processorsList) {
            String processorId = (String) mbeanServer.getAttribute(on, "ProcessorId");
            LOGGER.debug("Processing processorId \"{}\"", processorId);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");
            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String normalizedUri = URISupport.normalizeUri(expression);

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

                ConsumerInfo consumerInfo = new ConsumerInfo(routeId, endpointBaseUri, "pollEnrich", false);
                ConsumerUtils.addConsumerInfoIfNotInList(consumersInfo, consumerInfo, LOGGER);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
                EndpointUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);

            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                String endpointUri = URLDecoder.decode(normalizedUri, "UTF-8");

                ConsumerInfo consumerInfo = new ConsumerInfo(routeId, endpointUri, "pollEnrich", true);
                ConsumerUtils.addConsumerInfo(consumersInfo, consumerInfo, LOGGER);

            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

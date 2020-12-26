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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RecipientListInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientListInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServerConnection mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException, IOException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedRecipientList"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = new ArrayList<>();

        CollectionUtils.addAll(processorsList, processorsSet);

        for (ObjectName on : processorsList) {
            String processorId = (String) mbeanServer.getAttribute(on, "ProcessorId");
            LOGGER.debug("Processing processorId \"{}\"", processorId);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");
            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String uriDelimiter = (String) mbeanServer.getAttribute(on, "UriDelimiter");
            String[] recipientList = expression.split(uriDelimiter);

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                for (String recipient : recipientList) {
                    String normalizedUri = EndpointHelper.normalizeEndpointUri(recipient);
                    String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

                    ProducerInfo producerInfo = new ProducerInfo(routeId, endpointBaseUri, "recipientList", false);
                    ProducerUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                    EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
                    EndpointUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
                }

            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                for (String recipient : recipientList) {
                    String normalizedUri = EndpointHelper.normalizeEndpointUri(recipient);
                    String endpointUri = URLDecoder.decode(normalizedUri, "UTF-8");

                    ProducerInfo producerInfo = new ProducerInfo(routeId, endpointUri, "recipientList", true);
                    ProducerUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);
                }

            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

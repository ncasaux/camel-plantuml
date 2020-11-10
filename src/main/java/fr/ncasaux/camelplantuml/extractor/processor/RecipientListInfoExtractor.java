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

public class RecipientListInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientListInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException, UnsupportedEncodingException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedRecipientList"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = new ArrayList<>();

        CollectionUtils.addAll(processorsList, processorsSet);

        for (int index = 0; index < processorsList.size(); index++) {
            ObjectName on = processorsList.get(index);

            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String uriDelimiter = (String) mbeanServer.getAttribute(on, "UriDelimiter");

            String[] recipientList = expression.split(uriDelimiter);

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                for (int recipientIndex = 0; recipientIndex < recipientList.length; recipientIndex++) {
                    String recipient = recipientList[recipientIndex];

                    String normalizedUri = URISupport.normalizeUri(recipient);
                    String endpointBaseUri = URLDecoder.decode(getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

                    ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                            endpointBaseUri, "recipientList", false);

                    ListUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                    EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_recipientlist_".concat(String.valueOf(index))
                            .concat("_recipient_").concat(String.valueOf(recipientIndex)));
                    MapUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointBaseUri, endpointBaseUriInfo, LOGGER);
                }
            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                for (String recipient : recipientList) {
                    String normalizedUri = URISupport.normalizeUri(recipient);
                    String endpointUri = URLDecoder.decode(normalizedUri, "UTF-8");

                    ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                            endpointUri, "recipientList", true);
                    ListUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);
                }
            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

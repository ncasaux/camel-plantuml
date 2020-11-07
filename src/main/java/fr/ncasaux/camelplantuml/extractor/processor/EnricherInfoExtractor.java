package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import fr.ncasaux.camelplantuml.utils.MapUtils;
import org.apache.camel.support.EndpointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EnricherInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnricherInfoExtractor.class);

    public static void getProcessorsInfo(MBeanServer mbeanServer,
                                         ArrayList<ProducerInfo> producersInfo,
                                         HashMap<String, EndpointUriInfo> endpointUrisInfo,
                                         HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo)
            throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, URISyntaxException {

        QueryExp exp = Query.eq(Query.classattr(), Query.value("org.apache.camel.management.mbean.ManagedEnricher"));
        Set<ObjectName> processorsSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=processors,*"), exp);
        List<ObjectName> processorsList = List.copyOf(processorsSet);

        for (int index = 0; index < processorsList.size(); index++) {
            ObjectName on = processorsList.get(index);

            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");

            String normalizedUri = EndpointHelper.normalizeEndpointUri(expression);
            String endpointUri = URLDecoder.decode(normalizedUri, StandardCharsets.UTF_8);

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "enrich", false);
                ListUtils.addProducerInfoIfNotInList(producersInfo, producerInfo, LOGGER);

                EndpointUriInfo endpointUriInfo = new EndpointUriInfo(normalizedUri);
                MapUtils.addEndpointUriInfo(endpointUrisInfo, endpointUri, endpointUriInfo, LOGGER);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo("endpoint_enricher_".concat(String.valueOf(index)));
                MapUtils.addEndpointBaseUriInfo(endpointBaseUrisInfo, endpointUriInfo.getEndpointBaseUri(), endpointBaseUriInfo, LOGGER);

            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                ProducerInfo producerInfo = new ProducerInfo((String) mbeanServer.getAttribute(on, "RouteId"),
                        endpointUri, "enrich", true);
                ListUtils.addProducerInfo(producersInfo, producerInfo, LOGGER);

            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

package fr.ncasaux.camelplantuml.extractor.processor;

import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.EndpointUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.utils.ListUtils;
import org.apache.camel.support.EndpointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.net.URI;
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

            ProducerInfo producerInfo = new ProducerInfo();

            String expression = (String) mbeanServer.getAttribute(on, "Expression");
            String expressionLanguage = (String) mbeanServer.getAttribute(on, "ExpressionLanguage");
            String normalizedEndpointUri = EndpointHelper.normalizeEndpointUri(expression);
            String endpointUri = URLDecoder.decode(normalizedEndpointUri, StandardCharsets.UTF_8);

            producerInfo.setRouteId((String) mbeanServer.getAttribute(on, "RouteId"));
            producerInfo.setEndpointUri(endpointUri);
            producerInfo.setProcessorType("enrich");

            if (expressionLanguage.equalsIgnoreCase("constant")) {
                producerInfo.setUseDynamicEndpoint(false);
                ListUtils.addProducerInfoIfNotInList(producersInfo, producerInfo);

                EndpointUriInfo endpointUriInfo = new EndpointUriInfo();
                URI uri = new URI(normalizedEndpointUri);
                String endpointBaseUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment()).toString();
                endpointUriInfo.setEndpointBaseUri(URLDecoder.decode(endpointBaseUri, StandardCharsets.UTF_8));
                endpointUrisInfo.put(endpointUri, endpointUriInfo);
                LOGGER.info("{} with id \"{}\" added to the map of endpointUris", endpointUriInfo.toString(), endpointUri);

                EndpointBaseUriInfo endpointBaseUriInfo = new EndpointBaseUriInfo();
                endpointBaseUriInfo.setDiagramElementId("endpoint_enricher_".concat(String.valueOf(index)));
                endpointBaseUrisInfo.put(URLDecoder.decode(endpointBaseUri, StandardCharsets.UTF_8), endpointBaseUriInfo);
                LOGGER.info("EndpointBaseUri with id \"{}\" added to the map of endpointBaseUris", endpointBaseUri);
            } else if (expressionLanguage.equalsIgnoreCase("simple")) {
                producerInfo.setUseDynamicEndpoint(true);
                ListUtils.addProducerInfo(producersInfo, producerInfo);
            } else {
                LOGGER.info("Expression \"{}({})\" can not be used to get an URI", expressionLanguage, expression);
            }
        }
    }
}

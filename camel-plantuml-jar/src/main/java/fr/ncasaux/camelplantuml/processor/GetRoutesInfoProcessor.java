package fr.ncasaux.camelplantuml.processor;

import fr.ncasaux.camelplantuml.extractor.RoutesInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.*;
import fr.ncasaux.camelplantuml.generator.*;
import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import fr.ncasaux.camelplantuml.model.query.Parameters;
import fr.ncasaux.camelplantuml.utils.JmxUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.util.ArrayList;
import java.util.HashMap;

public class GetRoutesInfoProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetRoutesInfoProcessor.class);

    public final static String[] routeIdFilters = {"camelplantuml.*"};
    public final static String[] endpointBaseUriFilters = {".*camel-plantuml.*"};
    public final static String[] camelInternalEndpointSchemeFilters = {"direct","seda"};
    private final String jmxHost = System.getProperty("jmxHost");

    @Override
    public void process(Exchange exchange) throws Exception {

        Parameters parameters = new Parameters(Boolean.parseBoolean(exchange.getIn().getHeader("connectRoutes", String.class)));

        HashMap<String, RouteInfo> routesInfo = new HashMap<>(); //Hashmap key will be the routeId of the Camel route
        ArrayList<ConsumerInfo> consumersInfo = new ArrayList<>();
        ArrayList<ProducerInfo> producersInfo = new ArrayList<>();
        HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo = new HashMap<>(); //Hashmap key will be the endpoint base URI of the endpoint

        MBeanServerConnection mbeanServer;

        if (jmxHost == null) {
            LOGGER.info("Getting MBean server");
            mbeanServer = exchange.getContext().getManagementStrategy().getManagementAgent().getMBeanServer();
        } else {
            LOGGER.info("Getting MBean server from provided jmxHost \"".concat(jmxHost).concat("\""));
            JMXConnector connector = JmxUtils.getConnector(jmxHost);
            mbeanServer = connector.getMBeanServerConnection();
        }

        LOGGER.info("Processing routes");
        RoutesInfoExtractor.getRoutesInfo(mbeanServer, routesInfo, consumersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing SendProcessor processors");
        SendProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing SendDynamicProcessor processors");
        SendDynamicProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo);

        LOGGER.info("Processing Enricher processors");
        EnricherInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing PollEnricher processors");
        PollEnricherInfoExtractor.getProcessorsInfo(mbeanServer, consumersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing WireTapProcessor processors");
        WireTapProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing RecipientList processors");
        RecipientListInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Generating PlantUML diagram");
        String umlString = HeaderDiagramGenerator.generateUmlString(exchange.getContext().getName())
                .concat(RoutesDiagramGenerator.generateUmlString(routesInfo))
                .concat(EndpointsDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, parameters))
                .concat(ConsumersDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, routesInfo, parameters))
                .concat(ProducersDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, routesInfo, parameters))
                .concat(FooterDiagramGenerator.generateUmlString());

        exchange.getIn().setHeader("content-type", "text/plain;charset=utf-8");
        exchange.getIn().setBody(umlString);
    }
}

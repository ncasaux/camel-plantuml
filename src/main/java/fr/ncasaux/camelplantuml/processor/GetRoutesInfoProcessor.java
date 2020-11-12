package fr.ncasaux.camelplantuml.processor;

import fr.ncasaux.camelplantuml.extractor.EndpointsInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.RoutesInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.*;
import fr.ncasaux.camelplantuml.generator.*;
import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.EndpointBaseUriInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import java.util.ArrayList;
import java.util.HashMap;

public class GetRoutesInfoProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetRoutesInfoProcessor.class);

    public final static String[] routeIdFilters = {"camelplantuml.*"};
    public final static String[] endpointBaseUriFilters = {".*camel-plantuml.*", "rest:.*"};

    @Override
    public void process(Exchange exchange) throws Exception {

        boolean connectRoutes = Boolean.parseBoolean(exchange.getIn().getHeader("connectRoutes", String.class));

        HashMap<String, RouteInfo> routesInfo = new HashMap<>();
        ArrayList<ConsumerInfo> consumersInfo = new ArrayList<>();
        ArrayList<ProducerInfo> producersInfo = new ArrayList<>();
        HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo = new HashMap<>();

        LOGGER.info("Getting MBean server");
        MBeanServer mbeanServer = exchange.getContext().getManagementStrategy().getManagementAgent().getMBeanServer();

        LOGGER.info("Processing routes");
        RoutesInfoExtractor.getRoutesInfo(mbeanServer, routesInfo, consumersInfo);

        LOGGER.info("Processing endpoints");
        EndpointsInfoExtractor.getEndpointsInfo(mbeanServer, endpointBaseUrisInfo);

        LOGGER.info("Processing SendProcessor processors");
        SendProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo);

        LOGGER.info("Processing SendDynamicProcessor processors");
        SendDynamicProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo);

        LOGGER.info("Processing Enricher processors");
        EnricherInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing PollEnricher processors");
        PollEnricherInfoExtractor.getProcessorsInfo(mbeanServer, consumersInfo);

        LOGGER.info("Processing WireTapProcessor processors");
        WireTapProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing RecipientList processors");
        RecipientListInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointBaseUrisInfo);

        LOGGER.info("Generating PlantUML diagram");
        String umlString = HeaderDiagramGenerator.generateUmlString(exchange.getContext().getName())
                .concat(RoutesDiagramGenerator.generateUmlString(routesInfo))
                .concat(EndpointsDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, connectRoutes))
                .concat(ProducersDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, routesInfo, connectRoutes))
                .concat(ConsumersDiagramGenerator.generateUmlString(consumersInfo, producersInfo, endpointBaseUrisInfo, routesInfo, connectRoutes))
                .concat(FooterDiagramGenerator.generateUmlString());

        exchange.getIn().setHeader("content-type","text/plain;charset=utf-8");
        exchange.getIn().setBody(umlString);
    }
}

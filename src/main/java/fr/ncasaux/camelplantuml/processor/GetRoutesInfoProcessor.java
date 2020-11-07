package fr.ncasaux.camelplantuml.processor;

import fr.ncasaux.camelplantuml.extractor.ConsumersInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.EndpointsInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.ProducersInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.RoutesInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.EnricherInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.PollEnricherInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.SendDynamicProcessorInfoExtractor;
import fr.ncasaux.camelplantuml.extractor.processor.WireTapProcessorInfoExtractor;
import fr.ncasaux.camelplantuml.generator.*;
import fr.ncasaux.camelplantuml.model.*;
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
    public final static String[] endpointBaseUriFilters = {".*camel-plantuml.*"};

    @Override
    public void process(Exchange exchange) throws Exception {

        LOGGER.info("Getting MBean server");
        MBeanServer mbeanServer = exchange.getContext().getManagementStrategy().getManagementAgent().getMBeanServer();

        LOGGER.info("Processing routes");
        HashMap<String, RouteInfo> routesInfo = new HashMap<>();
        RoutesInfoExtractor.getRoutesInfo(mbeanServer, routesInfo);

        LOGGER.info("Processing endpoints");
        HashMap<String, EndpointUriInfo> endpointUrisInfo = new HashMap<>();
        HashMap<String, EndpointBaseUriInfo> endpointBaseUrisInfo = new HashMap<>();
        EndpointsInfoExtractor.getEndpointsInfo(mbeanServer, endpointUrisInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing producers");
        ArrayList<ProducerInfo> producersInfo = new ArrayList<>();
        ProducersInfoExtractor.getProducersInfo(mbeanServer, producersInfo);

        LOGGER.info("Processing consumers");
        ArrayList<ConsumerInfo> consumersInfo = new ArrayList<>();
        ConsumersInfoExtractor.getConsumersInfo(mbeanServer, consumersInfo);

        LOGGER.info("Processing SendDynamicProcessor processors");
        SendDynamicProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo);

        LOGGER.info("Processing Enricher processors");
        EnricherInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointUrisInfo, endpointBaseUrisInfo);

        LOGGER.info("Processing PollEnricher processors");
        PollEnricherInfoExtractor.getProcessorsInfo(mbeanServer, consumersInfo);

        LOGGER.info("Processing WireTapProcessor processors");
        WireTapProcessorInfoExtractor.getProcessorsInfo(mbeanServer, producersInfo, endpointUrisInfo, endpointBaseUrisInfo);

        LOGGER.info("Generating PlantUML diagram");
        String umlString = HeaderDiagramGenerator.generateUmlString()
                .concat(RoutesDiagramGenerator.generateUmlString(routesInfo))
                .concat(EndpointsDiagramGenerator.generateUmlString(endpointBaseUrisInfo))
                .concat(ProducersDiagramGenerator.generateUmlString(producersInfo, endpointUrisInfo, endpointBaseUrisInfo, routesInfo))
                .concat(ConsumersDiagramGenerator.generateUmlString(consumersInfo, endpointUrisInfo, endpointBaseUrisInfo, routesInfo))
                .concat(FooterDiagramGenerator.generateUmlString());

//        SourceStringReader reader = new SourceStringReader(umlString);
//        final ByteArrayOutputStream os = new ByteArrayOutputStream();
//
//        DiagramDescription diagramDescription = reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
//        os.close();
//
//        final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
//
//        exchange.getIn().setHeader(HTTP.CONTENT_TYPE,constant("image/svg+xml"));
//        exchange.getIn().setBody(svg);

//        Transcoder t = TranscoderUtil.getDefaultTranscoder();
//        String url = t.encode(umlString);

        exchange.getIn().setHeader("content-type", "text/plain;charset=utf-8");
        exchange.getIn().setBody(umlString);
    }
}

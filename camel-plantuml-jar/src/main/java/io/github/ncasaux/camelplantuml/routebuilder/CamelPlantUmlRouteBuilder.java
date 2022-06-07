package io.github.ncasaux.camelplantuml.routebuilder;

import io.github.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;
import io.github.ncasaux.camelplantuml.utils.JmxUtils;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.remote.JMXConnector;
import java.io.IOException;

public class CamelPlantUmlRouteBuilder extends EndpointRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelPlantUmlRouteBuilder.class);

    private Integer port = 8090;
    private String host = "localhost";
    private final String jmxHost = System.getProperty("jmxHost");

    /**
     * Creates a Camel RouteBuilder for camel-plantuml using default host and port for the HTTP endpoint
     */
    public CamelPlantUmlRouteBuilder() {
    }

    /**
     * Creates a Camel RouteBuilder for camel-plantuml using specified host and port for the HTTP endpoint
     * @param host Host of the HTTP endpoint of camel-plantuml
     * @param port Port of the HTTP endpoint of camel-plantuml
     */
    public CamelPlantUmlRouteBuilder(String host, Integer port) {
        this.port = port;
        this.host = host;
    }

    public void configure() throws IOException {

        if (jmxHost != null) {
            LOGGER.info("Testing MBean server connection from provided jmxHost \"".concat(jmxHost).concat("\""));
            JMXConnector connector = JmxUtils.getConnector(jmxHost);
            connector.getMBeanServerConnection();
            connector.close();
            LOGGER.info("MBean server connection successfully tested");
        }

        restConfiguration().component("netty-http").host(host).port(port);

        rest("camel-plantuml")
                .get("diagram.puml")
                    .param().name("connectRoutes").type(RestParamType.query).defaultValue("false").endParam()
                    .route().routeId("camel-plantuml-http-trigger")
                    .process(new GetRoutesInfoProcessor())
                .endRest()
        ;
    }
}

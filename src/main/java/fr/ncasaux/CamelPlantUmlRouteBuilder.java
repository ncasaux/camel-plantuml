package fr.ncasaux;

import fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

public class CamelPlantUmlRouteBuilder extends EndpointRouteBuilder {

    private Integer port = 8090;
    private String host = "localhost";

    public CamelPlantUmlRouteBuilder() {
    }

    public CamelPlantUmlRouteBuilder(String host, Integer port) {
        this.port = port;
        this.host = host;
    }

    public void configure() {

        restConfiguration()
                .component("netty-http")
                .host(host)
                .port(port)
        ;

        rest("camel-plantuml")
                .get("diagram.puml")
                    .route().id("camelplantuml-http-trigger")
                    .process(new GetRoutesInfoProcessor())
                .endRest()
        ;
    }
}

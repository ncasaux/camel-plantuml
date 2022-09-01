package DefaultExample;

import io.github.ncasaux.camelplantuml.routebuilder.CamelPlantUmlRouteBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

public class DefaultExampleWithHostAndPortTest extends CamelTestSupport {

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean useJmx() {
        return true;
    }

    @Test
    public void testMock() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:camel-plantuml-output");


        mock.expectedBodiesReceived("@startuml\n" +
                "\n" +
                "skinparam ArrowColor #Black\n" +
                "\n" +
                "skinparam rectangle {\n" +
                "  RoundCorner 20\n" +
                "}\n" +
                "\n" +
                "skinparam rectangle<<route>> {\n" +
                "  BorderColor #6C8EBF\n" +
                "  BackgroundColor #DAE8FC\n" +
                "}\n" +
                "\n" +
                "skinparam queue<<endpoint>> {\n" +
                "}\n" +
                "\n" +
                "skinparam queue<<static>> {\n" +
                "  BorderColor #B85450\n" +
                "  BackgroundColor #F8CECC\n" +
                "}\n" +
                "\n" +
                "skinparam queue<<dynamic>> {\n" +
                "  BorderColor #82B366\n" +
                "  BackgroundColor #D5E8D4\n" +
                "}\n" +
                "\n" +
                "footer Generated with camel-plantuml on %date(\"dd-MM-yyyy HH:mm\")\n" +
                "\n" +
                "' === Some useful settings for tweaking diagram layout ===\n" +
                "'left to right direction\n" +
                "'hide stereotype\n" +
                "'skinparam nodesep 50\n" +
                "'skinparam ranksep 50\n" +
                "'skinparam wrapWidth 250\n" +
                "\n" +
                "rectangle route_2ad9f14c90052cf9002f79d5f9fa54cb <<route>> as \"\n" +
                "mainRoute\n" +
                "....\n" +
                "<i>Route which handles processing of the message</i>\n" +
                "\"\n" +
                "\n" +
                "rectangle route_a4212b7daeffab7a6859963fce8df0c1 <<route>> as \"\n" +
                "fastTimerRoute\n" +
                "....\n" +
                "<i>Route which generates fast periodic events</i>\n" +
                "\"\n" +
                "\n" +
                "rectangle route_2c3a25b7f186d9a06118c863dc529551 <<route>> as \"\n" +
                "transformRoute\n" +
                "....\n" +
                "<i>Route which transforms the message</i>\n" +
                "\"\n" +
                "\n" +
                "rectangle route_e75ae82e12c615feefaf77105cf18e63 <<route>> as \"\n" +
                "slowTimerRoute\n" +
                "....\n" +
                "<i>Route which generates slow periodic events</i>\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_9259e64c942ad21bc3beebab69d91941 <<endpoint>><<static>> as \"\n" +
                "seda://endpoint1\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_c1749b7ed34e5395932c33f971ca4151 <<endpoint>><<static>> as \"\n" +
                "timer://foo\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_497a674fd655c44f28111e246619dc57 <<endpoint>><<static>> as \"\n" +
                "timer://bar\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_766e5055e2b624afb926345d34208088 <<endpoint>><<static>> as \"\n" +
                "direct://endpoint2\n" +
                "\"\n" +
                "\n" +
                "endpoint_497a674fd655c44f28111e246619dc57 --> route_a4212b7daeffab7a6859963fce8df0c1 : from\n" +
                "\n" +
                "endpoint_9259e64c942ad21bc3beebab69d91941 --> route_2ad9f14c90052cf9002f79d5f9fa54cb : from\n" +
                "\n" +
                "endpoint_c1749b7ed34e5395932c33f971ca4151 --> route_e75ae82e12c615feefaf77105cf18e63 : from\n" +
                "\n" +
                "endpoint_766e5055e2b624afb926345d34208088 --> route_2c3a25b7f186d9a06118c863dc529551 : from\n" +
                "\n" +
                "route_e75ae82e12c615feefaf77105cf18e63 --> endpoint_9259e64c942ad21bc3beebab69d91941 : to\n" +
                "\n" +
                "route_a4212b7daeffab7a6859963fce8df0c1 --> endpoint_9259e64c942ad21bc3beebab69d91941 : to\n" +
                "\n" +
                "queue dynamic_producer_endpoint_1846cdc72007895e6140e286368e7049 <<endpoint>><<dynamic>> as \"\n" +
                "mock://mock-${body}\n" +
                "\"\n" +
                "\n" +
                "route_2ad9f14c90052cf9002f79d5f9fa54cb --> dynamic_producer_endpoint_1846cdc72007895e6140e286368e7049 : toD\n" +
                "\n" +
                "route_2ad9f14c90052cf9002f79d5f9fa54cb --> endpoint_766e5055e2b624afb926345d34208088 : enrich\n" +
                "\n" +
                "@enduml\n");

        AdviceWith.adviceWith(context, "camel-plantuml-http-trigger", a -> {
                    a.weaveAddLast().transform(a.body().regexReplaceAll("\r", ""));
                    a.weaveAddLast().to("mock:camel-plantuml-output");
                }
        );

        context.start();

        template.sendBody("direct:camel-plantuml-generate-plantuml", null);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(timer("foo").period(5000)).routeId("slowTimerRoute")
                        .description("Route which generates slow periodic events")
                        .setBody(constant("slow"))
                        .to(seda("endpoint1")).id("_to01");

                from(timer("bar").period(1000)).routeId("fastTimerRoute")
                        .description("Route which generates fast periodic events")
                        .setBody(constant("fast"))
                        .to(seda("endpoint1")).id("_to02");

                from(seda("endpoint1")).routeId("mainRoute")
                        .description("Route which handles processing of the message")
                        .log(LoggingLevel.INFO, "${body}")
                        .enrich().constant("direct://endpoint2").id("_enrich01")
                        .toD(mock("mock-${body}")).id("_toD01");

                from(direct("endpoint2")).routeId("transformRoute")
                        .description("Route which transforms the message")
                        .transform(simple("${body}${body}"));

                getContext().addRoutes(new CamelPlantUmlRouteBuilder("localhost", 9090));

            }

        };
    }
}

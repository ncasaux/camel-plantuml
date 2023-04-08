package WireTap;

import io.github.ncasaux.camelplantuml.routebuilder.CamelPlantUmlRouteBuilder;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.timer;

public class WireTapDynamicTest extends CamelTestSupport {

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
                "rectangle route_1d0615e27dff16390ab2050dbb552ffc <<route>> as \"\n" +
                "wireTapStaticRoute1\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_c1749b7ed34e5395932c33f971ca4151 <<endpoint>><<static>> as \"\n" +
                "timer://foo\n" +
                "\"\n" +
                "\n" +
                "endpoint_c1749b7ed34e5395932c33f971ca4151 --> route_1d0615e27dff16390ab2050dbb552ffc : from\n" +
                "\n" +
                "queue dynamic_producer_endpoint_47b1af78d16f3d758a742cc808e97727 <<endpoint>><<dynamic>> as \"\n" +
                "mock://dummyDynamicWireTap\n" +
                "\"\n" +
                "\n" +
                "route_1d0615e27dff16390ab2050dbb552ffc --> dynamic_producer_endpoint_47b1af78d16f3d758a742cc808e97727 : wiretap\n" +
                "\n" +
                "@enduml\n");

        AdviceWith.adviceWith(context, "camel-plantuml-http-trigger", a -> {
                    a.weaveAddLast().transform(a.body().regexReplaceAll("\r", ""));
                    a.weaveAddLast().to("mock:camel-plantuml-output");
                }
        );

        context.start();

        template.sendBody("direct:camel-plantuml-generate-plantuml", null);
        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(timer("foo").period(5000)).routeId("wireTapStaticRoute1")
                        .wireTap("mock://dummyDynamicWireTap").id("_wireTap01")
                        .end();

                getContext().addRoutes(new CamelPlantUmlRouteBuilder());

            }

        };
    }
}

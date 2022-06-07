package RecipientList;

import io.github.ncasaux.camelplantuml.routebuilder.CamelPlantUmlRouteBuilder;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.timer;

public class RecipientListSimpleTest extends CamelTestSupport {

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
                "rectangle route_62fbcd625ec544f809248b42b7069ef6 <<route>> as \"\n" +
                "recipientListSimpleRoute1\n" +
                "\"\n" +
                "\n" +
                "queue endpoint_c1749b7ed34e5395932c33f971ca4151 <<endpoint>><<static>> as \"\n" +
                "timer://foo\n" +
                "\"\n" +
                "\n" +
                "endpoint_c1749b7ed34e5395932c33f971ca4151 --> route_62fbcd625ec544f809248b42b7069ef6 : from\n" +
                "\n" +
                "queue dynamic_producer_endpoint_733ba1f07cf27f4e3ce3da43b65eaf79 <<endpoint>><<dynamic>> as \"\n" +
                "ock://dummyRecipient-${body}\n" +
                "\"\n" +
                "\n" +
                "route_62fbcd625ec544f809248b42b7069ef6 --> dynamic_producer_endpoint_733ba1f07cf27f4e3ce3da43b65eaf79 : recipientList\n" +
                "\n" +
                "@enduml\n");

        AdviceWith.adviceWith(context, "camel-plantuml-http-trigger", a -> {
                    a.weaveAddLast().to("mock:camel-plantuml-output");
                    a.replaceFromWith("direct:camel-plantuml-http-trigger");
                }
        );

        context.start();

        template.sendBody("direct:camel-plantuml-http-trigger", null);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(timer("foo").period(5000)).routeId("recipientListSimpleRoute1")
                        .setBody(constant("bar"))
                        .recipientList().simple("ock://dummyRecipient-${body}").id("_recipientList01");

                getContext().addRoutes(new CamelPlantUmlRouteBuilder());

            }

        };
    }
}

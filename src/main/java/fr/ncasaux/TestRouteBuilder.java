package fr.ncasaux;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

//public class TestRouteBuilder extends RouteBuilder {
public class TestRouteBuilder extends EndpointRouteBuilder {

    public void configure() {


        from("seda:hello?concurrentConsumers=3")
                .transform().constant("Hello World")
                .to("direct://out")
                ;

        from("timer://foo?delay=1000").autoStartup(false)
                .description("aouééé!")
                .to("seda://hello?discardIfNoConsumers=true")
                .setBody(constant("1"))
                .pollEnrich().constant("seda://aaa")
                .recipientList(constant("zz,bb"))
                .recipientList(simple("seda:a-${body},seda:b-${body}"))
                .recipientList(header("foo"), ",")
                .recipientList(xpath("/xmlpath"))
                .recipientList(exchangeProperty("aa"))
        ;

//        from(timer("bar"))
//                .setBody(constant("2"))
//                .enrich(seda("test2-${body}"));

//        from(timer("foo"))
//                .routeDescription("Permet de déclencher le mode COUNT via une requête HTTP")
//                .setBody(constant("1"))
//                .wireTap("seda://test-${body}").dynamicUri(true)
//                .wireTap("seda://test-${body}").dynamicUri(false)
//                .wireTap("seda://test").dynamicUri(false)
//        ;
//
//        from(timer("bar"))
//                .setBody(constant("2"))
//                .pollEnrich(seda("test2-${body}"));
//
//        from(timer("biz"))
//                .setBody(constant("3"))
//                .enrich().constant(seda("test3-${body}"));
//
//        from(seda("test").concurrentConsumers(3))
//                .id("route1_id")
//                .description("route 1 description")
//                .wireTap("seda://test-${body}").dynamicUri(false)
//                .toD(seda("${body}"))
//                .transform().constant("Hello World");


//        from(direct("test2"))
//                .id("route3_id")
//                .description("route 3 description")
//                .transform().constant("Hello World");

//        from(timer("foo").delay(500))
//                .id("route2_id")
//                .description("route 2 description")
////                .to("direct:test")
//                .to(direct("abc"))
////                .to("log:myLog")
//                .to(seda("test").discardIfNoConsumers(true))
////                .setBody(constant("2"))
//                .toD(seda("elasticsearch-rest://elasticsearch?indexName=servicechecker-${exchangeProperty.mode.toLowerCase()}mode&operation=Index"))
////                .to(direct("pollEndpoint"))
//                .setBody(constant("2"))
//                .enrich().constant("direct://test2")
////                .enrich(seda("test-${body}").discardIfNoConsumers(true))
////                .enrich().constant("test-${superbody}")
////        .to(seda("test/a/b").advanced().synchronous(false))
//                .enrich().constant("direct://constantEnrich${body}?synchronous=true")
////                .enrich(direct("directEnrichEndpoint"))
////                .enrich("seda://test1-${body}&timeout=1000&discardIfNoConsumers=true")
////                .enrich(seda("test2-${body}").timeout(1000).discardIfNoConsumers(true))
////                .enrich().constant(seda("test1-${body}"))
////                .enrich(seda("test2-${body}"))
//                .enrich().simple("seda:${body}")
////                .enrich().header("foo")
//                .pollEnrich(direct("directPollEnrichEndpoint").advanced().synchronous(false))
////                .pollEnrich(seda("sedaPollEnrichEndpoint"))
//                .pollEnrich().simple("seda:${body}")
//                .pollEnrich().simple("seda:${body}")
////                .pollEnrich("seda:titi")
//                .pollEnrich().header("ff")
////                .pollEnrich().constant("seda:${stupid}")
////                .pollEnrich(seda("test33-${body}"))
////                .enrich(seda("test"))
////                .to(log("mylogger").level(LoggingLevel.INFO.toString()))
////                .enrich().xpath("aa")
////                .recipientList(constant("zz,bb"))
////                .recipientList(simple("seda:a,seda:b"))
////                .recipientList(header("foo"),",")
////                .recipientList(xpath("/xmlpath"))
////                .recipientList(exchangeProperty("aa"))
////                .multicast().to("direct:x","direct:y")
//        ;
////        from(seda("test").concurrentConsumers(3)).transform().constant("Hello World 1");
////        from(direct("test2")).transform().constant("Hello World");
////        from(direct("test3")).transform().constant("Hello World");


    }

}

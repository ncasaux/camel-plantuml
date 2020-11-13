# Overview
camel-plantuml is a tool which helps to genereate [PlantUML](https://plantuml.com/) diagrams describing Apache [Camel](https://camel.apache.org/) routes. 

It allows to have diagrams where we can see interactions between endpoints and routes.

If you consider following routes:
```
from(timer("foo").period(5000)).id("slowTimerRoute")
        .description("Route which generates slow periodic events")
        .setBody(constant("slow"))
        .to(seda("endpoint1"));

from(timer("bar").period(1000)).id("fastTimerRoute")
        .description("Route which generates fast periodic events")
        .setBody(constant("fast"))
        .to(seda("endpoint1"));

from(seda("endpoint1")).id("mainRoute")
        .description("Route which handles processing of the message")
        .log(LoggingLevel.INFO, "${body}")
        .enrich().constant("direct://endpoint2")
        .toD(mock("mock-${body}"));

from(direct("endpoint2")).id("transformRoute")
        .description("Route which transforms the message")
        .transform(simple("${body}${body}"));
```
It will allow you generate this:

# How it works
It uses the Camel JMX MBeans (which are enabled by default in Camel), and particularly the ones related to routes and processors.

Following processors are handled:
- SendProcessor (`to`)
- SendDynamicProcessor (`toD`)
- Enricher (`enrich`)
- PollEnricher (`pollEnrich`)
- WireTapProcessor (`wireTap`)
- RecipientList (`recipientList`)

The PlantUML code is exposed through a configurable HTTP endpoint, and can be rendered afterwards as an image using PlantUML [webserver](http://www.plantuml.com/plantuml/uml "PlantUML webserver") or any other tool where PlantUML is available (VSCode, IntelliJ, your own PlantUML server...)

# Features
This tool generates PlantUML diagrams with following features:
- each route is rendered as a rectangle
- each static endpoint base URI is rendered as a queue with a "static" layout
- each dynamic endpoint URI is rendered as a queue with a "dynamic" layout
- each consumer is rendered as a labelled arrow (`from` or `pollEnrich`) which connects an endpoint to a route
- each producer is rendered as a labelled arrow (`to`,`toD`,`enrich`,`wireTap` or `recipientList`) which connects a route to an endpoint

# Versions





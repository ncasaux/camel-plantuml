## Overview
camel-plantuml is a tool which helps to generate [PlantUML](https://plantuml.com/) diagrams which describe Apache [Camel](https://camel.apache.org/) routes. 

It allows to have diagrams where you can see interactions between endpoints and routes.

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
It will allow you to generate these images:

- with all endpoints:

![](images/example1.full.svg)

- without the "internal" endpoints:

![](images/example1.light.svg)

This tool is intended to be a software documentation tool, it is not intended to be a monitoring tool with real-time metrics or statistics.

## How it works
This tool needs to have a running Camel context instance, deployed with this tool as a dependency.
It uses the Camel JMX MBeans (which are enabled by default in Camel), and particularly the ones related to routes and processors.

Following processors are handled:
- SendProcessor (`to`)
- SendDynamicProcessor (`toD`)
- Enricher (`enrich`)
- PollEnricher (`pollEnrich`)
- WireTapProcessor (`wireTap`)
- RecipientList (`recipientList`)

It parses the processors to extract URI(s) information. 
If an expression is found, then, based on the language of the expression:
1. If the language is `constant`, it will consider it as a static endpoint (which could be used in other processors or routes).
2. If the language is `simple`, it will consider it as a dynamic endpoint (with no possible link with other processors or routes).
3. Otherwise, it will ignore the endpoint.

The PlantUML code is exposed through a configurable HTTP endpoint, so it can be re-worked, and finally rendered as an image.

## Features
This tool generates PlantUML diagrams with following features:
- each route is rendered as a rectangle, with its id and description
- each static endpoint base URI is rendered as a queue with a "static" layout.
- each dynamic endpoint URI is rendered as a queue with a "dynamic" layout.
- each consumer is rendered as a labelled arrow (`from` or `pollEnrich`) which connects an endpoint to a route.
- each producer is rendered as a labelled arrow (`to`,`toD`,`enrich`,`wireTap` or `recipientList`) which connects a route to an endpoint.
- it is possible to connect the routes to each other, if you don't want to display the "internal" endpoints on the diagram.

## Versions
There is a version for the two Camel major versions. Both versions uses Java `1.8`.

### Camel 2.x
The jar to use is `camel2-plantuml`. It has been built with Camel version `2.20.4`.
The jar is a OSGi bundle, and can be used with Apache ServiceMix/Apache Karaf.

### Camel 3.x
The jar to use is `camel3-plantuml`. It has been built with Camel version `3.4.4`.
The jar is a OSGi bundle, and can be used with Apache ServiceMix/Apache Karaf.

## How to use ?

### 1. Download the jar :
Download the jar which matches the Camel version you use from [here](https://github.com/ncasaux?tab=packages&repo_name=camel-plantuml)


### 2. Add the dependency to your project:
If you use Camel **2.x**:
```
<dependency>
    <groupId>fr.ncasaux</groupId>
    <artifactId>camel2-plantuml</artifactId>
    <version>{{version}}</version>
</dependency>
```
If you use Camel **3.x**:
```
<dependency>
    <groupId>fr.ncasaux</groupId>
    <artifactId>camel3-plantuml</artifactId>
    <version>{{version}}</version>
</dependency>
```

### 3. Add the route builder to your Camel context:
`getContext().addRoutes(new CamelPlantUmlRouteBuilder());`

Default host is `localhost`, default port is `8090`, but you can overide them:

`getContext().addRoutes(new CamelPlantUmlRouteBuilder("localhost", 8090));`

### 4. Start your Camel context, and open a browser:
To have all the endpoints, go to:

`http://{{host}}:{{port}}/camel-plantuml/diagram.puml`

To connect routes directly (and hide "internal" endpoints), add the following parameter:

`http://{{host}}:{{port}}/camel-plantuml/diagram.puml?connectRoutes=true`

### 5. Render the PlantUML code:
There are multiple options: 
- You can install PlantUML extension on your IDE, and graphviz on your computer to render locally (the best option).
- You can use a web browser extension to direcly render the code. There are extensions for Chrome and Firefox. 
- You can use the official PlantUML [webserver](http://www.plantuml.com/plantuml/uml "PlantUML webserver") and copy/paste the diagram.

## Contributing
Any suggestion, remark, or question? Feel free to create an issue and/or to contribute by forking and making pull requests!

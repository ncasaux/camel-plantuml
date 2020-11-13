# Overview
camel-plantuml is a tool which helps to genereate [PlantUML](https://plantuml.com/ "PlantUML") diagrams describing Apache [Camel](https://camel.apache.org/ "Camel") routes. It allows to have diagrams where we can see interactions between endpoints and routes.

# How it works
It uses the Camel JMX MBeans (which are enabled by default in Camel), and particularly the ones related to Camel routes and Camel processors.

Following processors are handled:

The PlantUML diagram **code** (not image) is exposed through an HTTP endpoint, and can be rendered afterwards using PlantUML [webserver](http://www.plantuml.com/plantuml/uml "PlantUML webserver") or any other tool where PlantUML is available (VSCode, IntelliJ, your own PlantUML server...)

# Features
This tool generates PlantUML diagrams with following features:
- each route is rendered as a rectangle
- each static endpoint base URI is rendered as a queue with a "static" layout
- each dynamic endpoint URI is rendered as a queue with a "dynamic" layout
- each consumer is rendered as an arrow connecting an endpoint to a route
- each producer is rendered as an arrow connecting a route to an endpoint





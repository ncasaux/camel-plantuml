# Overview
camel-plantuml is a tool which helps to genereate [PlantUML](https://plantuml.com/ "PlantUML") diagrams describing Apache [Camel](https://camel.apache.org/ "Camel") routes. It allows to have diagrams where we can see interactions between endpoints and routes.



It uses the Camel JMX Mbeans, and particularly the ones related to routes and processors.


This tool generates PlantUML diagrams with following features:
- each route is rendered as a rectangle
- each static endpoint base URI is rendered as a queue
- each dynamic endpoint URI is rendered as a queue
- each consumer is rendered as an arrow connecting an endpoint to a route
- each producer is rendered as an arrow connecting a route to an endpoint

package io.github.ncasaux;

import io.github.ncasaux.camelplantuml.routebuilder.CamelPlantUmlRouteBuilder;

import org.apache.camel.main.Main;

public class CamelPlantUml {

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.addRouteBuilder(new CamelPlantUmlRouteBuilder());
        main.run(args);
    }
}


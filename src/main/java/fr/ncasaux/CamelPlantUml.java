package fr.ncasaux;

import org.apache.camel.main.Main;

public class CamelPlantUml {

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new CamelPlantUmlRouteBuilder("localhost",9090));
        main.configure().addRoutesBuilder(new TestRouteBuilder());
        main.run(args);
    }
}


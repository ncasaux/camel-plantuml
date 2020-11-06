package fr.ncasaux.camelplantuml.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FooterDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FooterDiagramGenerator.class);

    public static String generateUmlString() throws URISyntaxException, IOException {

        return Files.readString(Paths.get(ClassLoader.getSystemResource("plantuml/footerTemplate").toURI()), StandardCharsets.UTF_8)
                .concat("\n");
    }
}
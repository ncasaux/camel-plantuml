package fr.ncasaux.camelplantuml.generator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FooterDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FooterDiagramGenerator.class);

    public static String generateUmlString() throws IOException {

        return IOUtils.toString(Objects.requireNonNull(FooterDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/footerTemplate")), StandardCharsets.UTF_8);
    }
}
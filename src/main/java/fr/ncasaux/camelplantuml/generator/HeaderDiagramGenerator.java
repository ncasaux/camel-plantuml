package fr.ncasaux.camelplantuml.generator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HeaderDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderDiagramGenerator.class);

    public static String generateUmlString() throws IOException {

        return IOUtils.toString(Objects.requireNonNull(HeaderDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/headerTemplate")),StandardCharsets.UTF_8);
    }
}
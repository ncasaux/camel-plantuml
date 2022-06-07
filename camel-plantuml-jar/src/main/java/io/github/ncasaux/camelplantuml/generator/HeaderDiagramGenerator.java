package io.github.ncasaux.camelplantuml.generator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HeaderDiagramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderDiagramGenerator.class);

    public static String generateUmlString(String contextName) throws IOException {

        String umlHeaderTemplate = IOUtils.toString(Objects.requireNonNull(HeaderDiagramGenerator.class.getClassLoader().getResourceAsStream("plantuml/headerTemplate")), StandardCharsets.UTF_8);
//        String umlString = "";
//
//        umlString = umlString
//                .concat(StringUtils.replaceEach(umlHeaderTemplate,
//                        new String[]{"%%diagramName%%"},
//                        new String[]{contextName}))
//                .concat("\n\n")
//        ;
//        return umlString;
        return umlHeaderTemplate.concat("\n\n");
    }
}
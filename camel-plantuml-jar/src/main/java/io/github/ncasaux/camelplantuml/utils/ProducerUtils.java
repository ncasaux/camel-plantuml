package io.github.ncasaux.camelplantuml.utils;

import io.github.ncasaux.camelplantuml.model.ProducerInfo;
import org.slf4j.Logger;

import java.util.ArrayList;

public class ProducerUtils {

    public static void addProducerInfoIfNotInList(ArrayList<ProducerInfo> al, ProducerInfo pi, Logger LOGGER) {
        if (!al.contains(pi)) {
            al.add(pi);
            LOGGER.info("{} added to the list of producers ", pi.toString());
        } else {
            LOGGER.info("{} already in the list of producers", pi.toString());
        }
    }

    public static void addProducerInfo(ArrayList<ProducerInfo> al, ProducerInfo pi, Logger LOGGER) {
        al.add(pi);
        LOGGER.info("{} added to the list of producers ", pi.toString());
    }
}

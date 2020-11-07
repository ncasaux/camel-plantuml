package fr.ncasaux.camelplantuml.utils;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import org.slf4j.Logger;

import java.util.ArrayList;

public class ListUtils {

    public static void addConsumerInfoIfNotInList(ArrayList<ConsumerInfo> al, ConsumerInfo ci, Logger LOGGER) {
        if (!al.contains(ci)) {
            al.add(ci);
            LOGGER.info("{} added to the list of consumers ", ci.toString());
        } else {
            LOGGER.info("{} already in the list of consumers", ci.toString());
        }
    }

    public static void addConsumerInfo(ArrayList<ConsumerInfo> al, ConsumerInfo ci, Logger LOGGER) {
        al.add(ci);
        LOGGER.info("{} added to the list of consumers ", ci.toString());
    }

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

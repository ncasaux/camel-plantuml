package fr.ncasaux.camelplantuml.utils;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.ProducerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ListUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListUtils.class);

    public static void addConsumerInfoIfNotInList(ArrayList<ConsumerInfo> al, ConsumerInfo ci) {
        if (!al.contains(ci)) {
            al.add(ci);
            LOGGER.info("{} added to the list of consumers ", ci.toString());
        } else {
            LOGGER.info("{} already in the list of consumers", ci.toString());
        }
    }

    public static void addConsumerInfo(ArrayList<ConsumerInfo> al, ConsumerInfo ci) {
        al.add(ci);
        LOGGER.info("{} added to the list of consumers ", ci.toString());
    }

    public static void addProducerInfoIfNotInList(ArrayList<ProducerInfo> al, ProducerInfo pi) {
        if (!al.contains(pi)) {
            al.add(pi);
            LOGGER.info("{} added to the list of producers ", pi.toString());
        } else {
            LOGGER.info("{} already in the list of producers", pi.toString());
        }
    }

    public static void addProducerInfo(ArrayList<ProducerInfo> al, ProducerInfo pi) {
        al.add(pi);
        LOGGER.info("{} added to the list of producers ", pi.toString());
    }

//    public static void addDynamicConsumerInfoIfNotInList(ArrayList<ConsumerInfo> al, ConsumerInfo ci) {
//        if (!al.contains(ci)) {
//            al.add(ci);
//            LOGGER.info("{} added to the list of dynamic consumers", ci.toString());
//        } else {
//            LOGGER.info("{} already in the list of dynamic consumers", ci.toString());
//        }
//    }
}

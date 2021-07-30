package io.github.ncasaux.camelplantuml.utils;

import io.github.ncasaux.camelplantuml.model.ConsumerInfo;
import org.slf4j.Logger;

import java.util.ArrayList;

public class ConsumerUtils {

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
}
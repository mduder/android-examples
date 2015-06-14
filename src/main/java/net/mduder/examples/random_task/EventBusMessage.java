package net.mduder.examples.random_task;

import java.util.TreeMap;

/**
 * Generic event bus message definition with a single optional argument.
 */
public class EventBusMessage {
    public enum MessageType {
        launchFragmentNumberDetails,
        launchFragmentStopWatch,
        launchFragmentMortgageLoanCalc,
        launchFragmentExchangeRateCalc,
        getDataExchangeRate,
        setDataExchangeRate
    }

    private MessageType messageType;
    private TreeMap<String, Object> args;

    EventBusMessage(MessageType messageType) {
        this.messageType = messageType;
        this.args = null;
    }

    EventBusMessage(MessageType messageType, TreeMap<String, Object> args) {
        this.messageType = messageType;
        this.args = args;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public TreeMap<String, Object> getArgs() {
        return this.args;
    }
}

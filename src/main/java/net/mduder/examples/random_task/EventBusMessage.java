package net.mduder.examples.random_task;

import java.util.LinkedHashMap;

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
    private LinkedHashMap<String, Object> args;

    EventBusMessage(MessageType messageType) {
        this.messageType = messageType;
        this.args = null;
    }

    EventBusMessage(MessageType messageType, LinkedHashMap<String, Object> args) {
        this.messageType = messageType;
        this.args = args;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public LinkedHashMap<String, Object> getArgs() {
        return this.args;
    }
}

package com.bank.payment.util;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import java.nio.charset.StandardCharsets;

public final class JmsPayloads {

    private JmsPayloads() {
    }

    public static String read(Message message) throws JMSException {
        if (message instanceof TextMessage text) {
            return text.getText();
        }
        if (message instanceof BytesMessage bytes) {
            bytes.reset();
            byte[] data = new byte[(int) bytes.getBodyLength()];
            bytes.readBytes(data);
            return new String(data, StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException(
                "Type de message non supporté : " + message.getClass().getSimpleName());
    }
}
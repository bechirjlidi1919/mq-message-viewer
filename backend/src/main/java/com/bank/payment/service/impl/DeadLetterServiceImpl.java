package com.bank.payment.service.impl;

import com.bank.payment.service.DeadLetterService;
import com.bank.payment.util.JmsPayloads;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterServiceImpl implements DeadLetterService {

    private final JmsTemplate jmsTemplate;
    private final String dlqName;

    public DeadLetterServiceImpl(JmsTemplate jmsTemplate,
                                 @Value("${app.mq.dlq}") String dlqName) {
        this.jmsTemplate = jmsTemplate;
        this.dlqName = dlqName;
    }

    @Override
    public void send(Message original, Exception cause) {
        jmsTemplate.send(dlqName, session -> {
            String body;
            try {
                body = JmsPayloads.read(original);
            } catch (IllegalArgumentException e) {
                body = "";
            }

            TextMessage dlqMessage = session.createTextMessage(body);
            dlqMessage.setJMSCorrelationID(original.getJMSCorrelationID());
            dlqMessage.setStringProperty("originalMessageId", original.getJMSMessageID());
            dlqMessage.setStringProperty("errorReason",
                    cause.getClass().getSimpleName() + " : " + cause.getMessage());
            return dlqMessage;
        });
    }
}
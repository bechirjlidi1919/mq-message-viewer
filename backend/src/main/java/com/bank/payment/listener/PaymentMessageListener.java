package com.bank.payment.listener;

import com.bank.payment.service.DeadLetterService;
import com.bank.payment.service.MessageIngestionService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentMessageListener.class);

    private final MessageIngestionService ingestionService;
    private final DeadLetterService deadLetterService;
    private final int maxAttempts;

    public PaymentMessageListener(MessageIngestionService ingestionService,
                                  DeadLetterService deadLetterService,
                                  @Value("${app.mq.max-attempts}") int maxAttempts) {
        this.ingestionService = ingestionService;
        this.deadLetterService = deadLetterService;
        this.maxAttempts = maxAttempts;
    }

    @JmsListener(id = "paymentListener", destination = "${app.mq.queue}")
    public void onMessage(Message message) throws Exception {
        try {
            ingestionService.ingest(message);
        } catch (Exception e) {
            int attempt = deliveryCount(message);
            if (attempt < maxAttempts) {
                log.warn("Échec du message {} (tentative {}/{}), nouvelle tentative",
                        message.getJMSMessageID(), attempt, maxAttempts);
                throw e;
            }
            log.error("Échec définitif du message {} après {} tentatives : envoi en DLQ",
                    message.getJMSMessageID(), attempt, e);
            deadLetterService.send(message, e);
        }
    }

    private int deliveryCount(Message message) throws JMSException {
        return message.propertyExists("JMSXDeliveryCount")
                ? message.getIntProperty("JMSXDeliveryCount")
                : 1;
    }
}
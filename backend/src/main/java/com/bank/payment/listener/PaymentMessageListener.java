package com.bank.payment.listener;

import com.bank.payment.service.MessageIngestionService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageListener {

    private final MessageIngestionService ingestionService;

    public PaymentMessageListener(MessageIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @JmsListener(id = "paymentListener", destination = "${app.mq.queue}")
    public void onMessage(Message message) throws JMSException {
        ingestionService.ingest(message);
    }
}
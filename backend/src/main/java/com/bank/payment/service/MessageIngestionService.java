package com.bank.payment.service;

import com.bank.payment.entity.MqMessage;
import com.bank.payment.repository.MqMessageRepository;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class MessageIngestionService {

    private static final Logger log = LoggerFactory.getLogger(MessageIngestionService.class);

    private final MqMessageRepository repository;
    private final String queueName;

    public MessageIngestionService(MqMessageRepository repository,
                                   @Value("${app.mq.queue}") String queueName) {
        this.repository = repository;
        this.queueName = queueName;
    }

    @Transactional
    public void ingest(Message message) throws JMSException {
        String messageId = message.getJMSMessageID();

        // Idempotence : un message déjà enregistré n'est pas réinséré
        if (repository.existsByMessageId(messageId)) {
            log.warn("Message {} déjà enregistré, ignoré", messageId);
            return;
        }

        long jmsTimestamp = message.getJMSTimestamp();
        MqMessage entity = new MqMessage(
                messageId,
                message.getJMSCorrelationID(),
                queueName,
                extractPayload(message),
                jmsTimestamp > 0 ? Instant.ofEpochMilli(jmsTimestamp) : null,
                Instant.now()
        );

        repository.save(entity);
        log.debug("Message {} enregistré (id={})", messageId, entity.getId());
    }

    private static String extractPayload(Message message) throws JMSException {
        if (message instanceof TextMessage text) {
            return text.getText();
        }
        if (message instanceof BytesMessage bytes) {
            byte[] data = new byte[(int) bytes.getBodyLength()];
            bytes.readBytes(data);
            return new String(data, StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException(
                "Type de message non supporté : " + message.getClass().getSimpleName());
    }
}
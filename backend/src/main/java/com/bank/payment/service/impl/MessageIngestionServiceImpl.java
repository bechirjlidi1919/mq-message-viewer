package com.bank.payment.service.impl;

import com.bank.payment.entity.MqMessage;
import com.bank.payment.repository.MqMessageRepository;
import com.bank.payment.service.MessageIngestionService;
import com.bank.payment.util.JmsPayloads;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class MessageIngestionServiceImpl implements MessageIngestionService {

    private static final Logger log = LoggerFactory.getLogger(MessageIngestionServiceImpl.class);

    private final MqMessageRepository repository;
    private final String queueName;

    public MessageIngestionServiceImpl(MqMessageRepository repository,
                                       @Value("${app.mq.queue}") String queueName) {
        this.repository = repository;
        this.queueName = queueName;
    }

    @Override
    @Transactional
    public void ingest(Message message) throws JMSException {
        String messageId = message.getJMSMessageID();

        if (repository.existsByMessageId(messageId)) {
            log.warn("Message {} déjà enregistré, ignoré", messageId);
            return;
        }

        long jmsTimestamp = message.getJMSTimestamp();
        MqMessage entity = new MqMessage(
                messageId,
                message.getJMSCorrelationID(),
                queueName,
                JmsPayloads.read(message),
                jmsTimestamp > 0 ? Instant.ofEpochMilli(jmsTimestamp) : null,
                Instant.now()
        );

        repository.save(entity);
        log.debug("Message {} enregistré (id={})", messageId, entity.getId());
    }
}
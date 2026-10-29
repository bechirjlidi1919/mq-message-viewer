package com.bank.payment.dto;

import com.bank.payment.entity.MqMessage;
import java.time.Instant;

public class MessageDto {

    private final Long id;
    private final String messageId;
    private final String correlationId;
    private final String queueName;
    private final Instant putTimestamp;
    private final Instant receivedAt;

    public MessageDto(Long id, String messageId, String correlationId,
                      String queueName, Instant putTimestamp, Instant receivedAt) {
        this.id = id;
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.queueName = queueName;
        this.putTimestamp = putTimestamp;
        this.receivedAt = receivedAt;
    }

    public static MessageDto from(MqMessage m) {
        return new MessageDto(m.getId(), m.getMessageId(), m.getCorrelationId(),
                m.getQueueName(), m.getPutTimestamp(), m.getReceivedAt());
    }

    public Long getId() { return id; }
    public String getMessageId() { return messageId; }
    public String getCorrelationId() { return correlationId; }
    public String getQueueName() { return queueName; }
    public Instant getPutTimestamp() { return putTimestamp; }
    public Instant getReceivedAt() { return receivedAt; }
}
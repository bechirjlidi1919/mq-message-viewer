package com.bank.payment.dto;

import com.bank.payment.entity.MqMessage;
import java.time.Instant;

public class MessageDetailDto {

    private final Long id;
    private final String messageId;
    private final String correlationId;
    private final String queueName;
    private final String payload;
    private final Instant putTimestamp;
    private final Instant receivedAt;

    public MessageDetailDto(Long id, String messageId, String correlationId, String queueName,
                            String payload, Instant putTimestamp, Instant receivedAt) {
        this.id = id;
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.queueName = queueName;
        this.payload = payload;
        this.putTimestamp = putTimestamp;
        this.receivedAt = receivedAt;
    }

    public static MessageDetailDto from(MqMessage m) {
        return new MessageDetailDto(
                m.getId(), m.getMessageId(), m.getCorrelationId(), m.getQueueName(),
                m.getPayload(), m.getPutTimestamp(), m.getReceivedAt());
    }

    public Long getId() { return id; }
    public String getMessageId() { return messageId; }
    public String getCorrelationId() { return correlationId; }
    public String getQueueName() { return queueName; }
    public String getPayload() { return payload; }
    public Instant getPutTimestamp() { return putTimestamp; }
    public Instant getReceivedAt() { return receivedAt; }
}
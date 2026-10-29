package com.bank.payment.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MQ_MESSAGE")
public class MqMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mq_message_seq")
    @SequenceGenerator(name = "mq_message_seq", sequenceName = "MQ_MESSAGE_SEQ", allocationSize = 50)
    private Long id;

    @Column(name = "MESSAGE_ID", nullable = false, length = 100)
    private String messageId;

    @Column(name = "CORRELATION_ID", length = 100)
    private String correlationId;

    @Column(name = "QUEUE_NAME", nullable = false, length = 48)
    private String queueName;

    @Lob
    @Column(name = "PAYLOAD")
    private String payload;

    @Column(name = "PUT_TIMESTAMP")
    private Instant putTimestamp;

    @Column(name = "RECEIVED_AT", nullable = false)
    private Instant receivedAt;

    protected MqMessage() {}

    public MqMessage(String messageId, String correlationId, String queueName,
                     String payload, Instant putTimestamp, Instant receivedAt) {
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.queueName = queueName;
        this.payload = payload;
        this.putTimestamp = putTimestamp;
        this.receivedAt = receivedAt;
    }

    public Long getId() { return id; }
    public String getMessageId() { return messageId; }
    public String getCorrelationId() { return correlationId; }
    public String getQueueName() { return queueName; }
    public String getPayload() { return payload; }
    public Instant getPutTimestamp() { return putTimestamp; }
    public Instant getReceivedAt() { return receivedAt; }
}
package com.bank.payment.dto;

import java.time.Instant;

public interface MessageDto {

    Long getId();

    String getMessageId();

    String getCorrelationId();

    String getQueueName();

    Instant getPutTimestamp();

    Instant getReceivedAt();
}
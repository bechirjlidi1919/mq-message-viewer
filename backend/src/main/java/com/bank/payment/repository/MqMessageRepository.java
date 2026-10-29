package com.bank.payment.repository;

import com.bank.payment.entity.MqMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MqMessageRepository extends JpaRepository<MqMessage, Long> {

    boolean existsByMessageId(String messageId);
}
package com.bank.payment.repository;

import com.bank.payment.entity.MqMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MqMessageRepository extends JpaRepository<MqMessage, Long> {

    boolean existsByMessageId(String messageId);

    List<MqMessage> findAllByOrderByReceivedAtDesc(Pageable pageable);

    List<MqMessage> findByPayloadContainingOrderByReceivedAtDesc(String search, Pageable pageable);

    long countByPayloadContaining(String search);

    List<MqMessage> findAllByOrderByReceivedAtDesc();
}
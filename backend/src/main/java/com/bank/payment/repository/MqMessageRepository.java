package com.bank.payment.repository;

import com.bank.payment.dto.MessageDto;
import com.bank.payment.entity.MqMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MqMessageRepository extends JpaRepository<MqMessage, Long> {

    boolean existsByMessageId(String messageId);

    Page<MessageDto> findAllBy(Pageable pageable);

    Page<MessageDto> findByPayloadContaining(String search, Pageable pageable);

    List<MessageDto> findAllByOrderByReceivedAtDesc();
}
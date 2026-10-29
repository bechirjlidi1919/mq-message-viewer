package com.bank.payment.service;

import com.bank.payment.dto.MessageDetailDto;
import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;
import com.bank.payment.entity.MqMessage;
import com.bank.payment.exception.MessageNotFoundException;
import com.bank.payment.repository.MqMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageQueryService {

    private static final int MAX_PAGE_SIZE = 100;

    private final MqMessageRepository repository;

    public MessageQueryService(MqMessageRepository repository) {
        this.repository = repository;
    }

    public PageResponse<MessageDto> search(String search, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        List<MqMessage> messages;
        long total;
        if (search == null || search.isBlank()) {
            messages = repository.findAllByOrderByReceivedAtDesc(pageable);
            total = repository.count();
        } else {
            messages = repository.findByPayloadContainingOrderByReceivedAtDesc(search.trim(), pageable);
            total = repository.countByPayloadContaining(search.trim());
        }

        List<MessageDto> content = messages.stream().map(MessageDto::from).toList();
        int totalPages = (int) Math.ceil((double) total / safeSize);

        return new PageResponse<>(content, safePage, safeSize, total, totalPages);
    }

    public List<MessageDto> findAll() {
        return repository.findAllByOrderByReceivedAtDesc().stream()
                .map(MessageDto::from)
                .toList();
    }

    public MessageDetailDto findById(Long id) {
        return repository.findById(id)
                .map(MessageDetailDto::from)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}
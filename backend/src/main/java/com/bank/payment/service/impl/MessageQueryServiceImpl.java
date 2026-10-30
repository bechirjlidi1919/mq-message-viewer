package com.bank.payment.service.impl;

import com.bank.payment.dto.MessageDetailDto;
import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;
import com.bank.payment.exception.MessageNotFoundException;
import com.bank.payment.repository.MqMessageRepository;
import com.bank.payment.service.MessageQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageQueryServiceImpl implements MessageQueryService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "receivedAt");

    private final MqMessageRepository repository;

    public MessageQueryServiceImpl(MqMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<MessageDto> search(String search, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, SORT);

        Page<MessageDto> result = (search == null || search.isBlank())
                ? repository.findAllBy(pageable)
                : repository.findByPayloadContaining(search.trim(), pageable);

        return PageResponse.from(result);
    }

    @Override
    public List<MessageDto> findAll() {
        return repository.findAllByOrderByReceivedAtDesc();
    }

    @Override
    public MessageDetailDto findById(Long id) {
        return repository.findById(id)
                .map(MessageDetailDto::from)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}
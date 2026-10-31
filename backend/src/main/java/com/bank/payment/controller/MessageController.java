package com.bank.payment.controller;

import com.bank.payment.dto.MessageDetailDto;
import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;
import com.bank.payment.service.MessageQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageQueryService queryService;

    public MessageController(MessageQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public PageResponse<MessageDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return queryService.search(search, page, size);
    }

    @GetMapping("/{id}")
    public MessageDetailDto get(@PathVariable Long id) {
        return queryService.findById(id);
    }
}
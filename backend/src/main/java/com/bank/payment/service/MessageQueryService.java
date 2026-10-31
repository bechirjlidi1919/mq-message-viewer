package com.bank.payment.service;

import com.bank.payment.dto.MessageDetailDto;
import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;

import java.util.List;

public interface MessageQueryService {

    PageResponse<MessageDto> search(String search, int page, int size);

    MessageDetailDto findById(Long id);
}
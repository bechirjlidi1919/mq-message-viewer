package com.bank.payment.service;

import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;
import com.bank.payment.exception.MessageNotFoundException;
import com.bank.payment.repository.MqMessageRepository;
import com.bank.payment.service.impl.MessageQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageQueryServiceTest {

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "receivedAt");

    private MqMessageRepository repository;
    private MessageQueryService service;

    @BeforeEach
    void setUp() {
        repository = mock(MqMessageRepository.class);
        service = new MessageQueryServiceImpl(repository);
    }

    @Test
    void doitRenvoyerUnePageAvecLeTotal() {
        Pageable pageable = PageRequest.of(0, 20, SORT);
        when(repository.findAllBy(pageable)).thenReturn(
                new PageImpl<>(List.of(mock(MessageDto.class), mock(MessageDto.class)), pageable, 45));

        PageResponse<MessageDto> result = service.search(null, 0, 20);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(45);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    @Test
    void doitUtiliserLaRechercheQuandUnTexteEstFourni() {
        Pageable pageable = PageRequest.of(0, 20, SORT);
        when(repository.findByPayloadContaining("test", pageable)).thenReturn(
                new PageImpl<>(List.of(mock(MessageDto.class)), pageable, 1));

        PageResponse<MessageDto> result = service.search("test", 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository, never()).findAllBy(any());
    }

    @Test
    void doitLimiterLaTailleDePageA100() {
        Pageable attendu = PageRequest.of(0, 100, SORT);
        when(repository.findAllBy(attendu)).thenReturn(Page.empty(attendu));

        service.search(null, 0, 5000);

        verify(repository).findAllBy(attendu);
    }

    @Test
    void doitLeverUneExceptionSiLeMessageNExistePas() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> service.findById(99L));
    }
}
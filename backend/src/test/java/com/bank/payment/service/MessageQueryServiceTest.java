package com.bank.payment.service;

import com.bank.payment.dto.MessageDto;
import com.bank.payment.dto.PageResponse;
import com.bank.payment.entity.MqMessage;
import com.bank.payment.exception.MessageNotFoundException;
import com.bank.payment.repository.MqMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageQueryServiceTest {

    private MqMessageRepository repository;
    private MessageQueryService service;

    @BeforeEach
    void setUp() {
        repository = mock(MqMessageRepository.class);
        service = new MessageQueryService(repository);
    }

    private MqMessage unMessage(String payload) {
        return new MqMessage("ID:1", null, "DEV.PAYMENT.IN", payload, Instant.now(), Instant.now());
    }

    @Test
    void doitRenvoyerUnePageAvecLeTotal() {
        when(repository.findAllByOrderByReceivedAtDesc(PageRequest.of(0, 20)))
                .thenReturn(List.of(unMessage("A"), unMessage("B")));
        when(repository.count()).thenReturn(45L);

        PageResponse<MessageDto> result = service.search(null, 0, 20);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(45);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    @Test
    void doitUtiliserLaRechercheQuandUnTexteEstFourni() {
        when(repository.findByPayloadContainingOrderByReceivedAtDesc("test", PageRequest.of(0, 20)))
                .thenReturn(List.of(unMessage("test-1")));
        when(repository.countByPayloadContaining("test")).thenReturn(1L);

        PageResponse<MessageDto> result = service.search("test", 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository, never()).findAllByOrderByReceivedAtDesc(any());
    }

    @Test
    void doitLimiterLaTailleDePageA100() {
        service.search(null, 0, 5000);

        verify(repository).findAllByOrderByReceivedAtDesc(PageRequest.of(0, 100));
    }

    @Test
    void doitLeverUneExceptionSiLeMessageNExistePas() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> service.findById(99L));
    }
}
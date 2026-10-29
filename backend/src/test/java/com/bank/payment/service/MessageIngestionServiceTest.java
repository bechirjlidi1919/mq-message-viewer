package com.bank.payment.service;

import com.bank.payment.entity.MqMessage;
import com.bank.payment.repository.MqMessageRepository;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageIngestionServiceTest {

    private MqMessageRepository repository;
    private MessageIngestionService service;

    @BeforeEach
    void setUp() {
        repository = mock(MqMessageRepository.class);
        service = new MessageIngestionService(repository, "DEV.PAYMENT.IN");
    }

    @Test
    void doitEnregistrerUnNouveauMessage() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getJMSMessageID()).thenReturn("ID:123");
        when(message.getText()).thenReturn("Paiement 100 EUR");
        when(repository.existsByMessageId("ID:123")).thenReturn(false);

        service.ingest(message);

        ArgumentCaptor<MqMessage> captor = ArgumentCaptor.forClass(MqMessage.class);
        verify(repository).save(captor.capture());
        MqMessage saved = captor.getValue();
        assertThat(saved.getMessageId()).isEqualTo("ID:123");
        assertThat(saved.getPayload()).isEqualTo("Paiement 100 EUR");
        assertThat(saved.getQueueName()).isEqualTo("DEV.PAYMENT.IN");
        assertThat(saved.getReceivedAt()).isNotNull();
    }

    @Test
    void neDoitPasEnregistrerUnMessageDejaPresent() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getJMSMessageID()).thenReturn("ID:123");
        when(repository.existsByMessageId("ID:123")).thenReturn(true);

        service.ingest(message);

        verify(repository, never()).save(any());
    }

    @Test
    void doitRejeterUnTypeDeMessageNonSupporte() throws Exception {
        Message message = mock(Message.class);
        when(message.getJMSMessageID()).thenReturn("ID:456");

        assertThrows(IllegalArgumentException.class, () -> service.ingest(message));
        verify(repository, never()).save(any());
    }
}
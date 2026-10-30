package com.bank.payment.listener;

import com.bank.payment.service.DeadLetterService;
import com.bank.payment.service.MessageIngestionService;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentMessageListenerTest {

    private MessageIngestionService ingestionService;
    private DeadLetterService deadLetterService;
    private PaymentMessageListener listener;
    private Message message;

    @BeforeEach
    void setUp() throws Exception {
        ingestionService = mock(MessageIngestionService.class);
        deadLetterService = mock(DeadLetterService.class);
        listener = new PaymentMessageListener(ingestionService, deadLetterService, 3);

        message = mock(Message.class);
        when(message.getJMSMessageID()).thenReturn("ID:1");
        when(message.propertyExists("JMSXDeliveryCount")).thenReturn(true);
    }

    @Test
    void doitEnregistrerSansPasserParLaDlq() throws Exception {
        listener.onMessage(message);

        verify(ingestionService).ingest(message);
        verify(deadLetterService, never()).send(any(), any());
    }

    @Test
    void doitRelancerLExceptionAvantLaDerniereTentative() throws Exception {
        when(message.getIntProperty("JMSXDeliveryCount")).thenReturn(1);
        doThrow(new RuntimeException("Oracle indisponible")).when(ingestionService).ingest(message);

        assertThrows(RuntimeException.class, () -> listener.onMessage(message));
        verify(deadLetterService, never()).send(any(), any());
    }

    @Test
    void doitEnvoyerEnDlqALaDerniereTentative() throws Exception {
        when(message.getIntProperty("JMSXDeliveryCount")).thenReturn(3);
        RuntimeException erreur = new IllegalArgumentException("Type de message non supporté");
        doThrow(erreur).when(ingestionService).ingest(message);

        listener.onMessage(message);

        verify(deadLetterService).send(message, erreur);
    }
}
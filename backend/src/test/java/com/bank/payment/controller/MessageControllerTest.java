package com.bank.payment.controller;

import com.bank.payment.dto.MessageDetailDto;
import com.bank.payment.exception.MessageNotFoundException;
import com.bank.payment.service.MessageQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageQueryService queryService;

    @Test
    void doitRenvoyerLeDetailDUnMessage() throws Exception {
        when(queryService.findById(1L)).thenReturn(new MessageDetailDto(
                1L, "ID:1", null, "DEV.PAYMENT.IN", "Bonjour MQ", Instant.now(), Instant.now()));

        mockMvc.perform(get("/api/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.payload").value("Bonjour MQ"));
    }

    @Test
    void doitRenvoyer404SiLeMessageNExistePas() throws Exception {
        when(queryService.findById(999L)).thenThrow(new MessageNotFoundException(999L));

        mockMvc.perform(get("/api/messages/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Message introuvable"));
    }

    @Test
    void doitRenvoyer400SiLIdNEstPasUnNombre() throws Exception {
        mockMvc.perform(get("/api/messages/abc"))
                .andExpect(status().isBadRequest());
    }
}
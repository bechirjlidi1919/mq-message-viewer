package com.bank.payment.service;

import jakarta.jms.Message;

public interface DeadLetterService {

    void send(Message original, Exception cause);
}
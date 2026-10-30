package com.bank.payment.service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

public interface MessageIngestionService {

    void ingest(Message message) throws JMSException;
}
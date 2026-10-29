package com.bank.payment.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(Long id) {
        super("Aucun message trouvé avec l'id " + id);
    }
}
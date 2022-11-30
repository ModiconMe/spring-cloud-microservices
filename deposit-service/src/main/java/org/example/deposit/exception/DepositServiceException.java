package org.example.deposit.exception;

public class DepositServiceException extends RuntimeException {

    public DepositServiceException() {
    }

    public DepositServiceException(String message) {
        super(message);
    }

}

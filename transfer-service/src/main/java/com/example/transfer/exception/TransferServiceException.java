package com.example.transfer.exception;

public class TransferServiceException extends RuntimeException {

    public TransferServiceException() {
    }

    public TransferServiceException(String message) {
        super(message);
    }

}

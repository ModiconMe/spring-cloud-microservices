package org.example.account.utils.exception;

public class AccountAlreadyExistException extends RuntimeException {

    public AccountAlreadyExistException() {
    }

    public AccountAlreadyExistException(String message) {
        super(message);
    }
}

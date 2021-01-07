package com.rukawa.sql.exception;

public class NoneAttributeException extends RuntimeException {

    public NoneAttributeException() {
        super("Found object attribute is null");
    }

    public NoneAttributeException(String message) {
        super(message);
    }

    public NoneAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoneAttributeException(Throwable cause) {
        super(cause);
    }

    public NoneAttributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

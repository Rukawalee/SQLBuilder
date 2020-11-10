package com.rukawa.sql.exception;

public class NoneExecutionException extends Exception {

    public NoneExecutionException() {
        super("Cannot find execution fields");
    }

    public NoneExecutionException(String message) {
        super(message);
    }

    public NoneExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoneExecutionException(Throwable cause) {
        super(cause);
    }

    public NoneExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

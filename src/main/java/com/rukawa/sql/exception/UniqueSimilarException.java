package com.rukawa.sql.exception;

public class UniqueSimilarException extends Exception {

    public UniqueSimilarException() {
        super("Unique similar field");
    }

    public UniqueSimilarException(String message) {
        super(message);
    }

    public UniqueSimilarException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueSimilarException(Throwable cause) {
        super(cause);
    }

    public UniqueSimilarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

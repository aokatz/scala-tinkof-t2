package org.tinkoff.task.two.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}

package org.example.forum.exception;

public class DuplicateTitleException extends RuntimeException {

    public DuplicateTitleException(String message) {
        super(message);
    }
}

package com.meandmyphone.chupacabraremote.exceptions;

public class ClientContextNotInitalizedException extends RuntimeException {

    public ClientContextNotInitalizedException(String message) {
        super(message);
    }

    public ClientContextNotInitalizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

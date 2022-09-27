package com.sendroids.usersyncas.exception;

public class ClientIdNotFoundException extends RuntimeException {
    public ClientIdNotFoundException() {
        super("Can not get client in security context.");
    }
}

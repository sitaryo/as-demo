package com.sendroids.usersyncas.exception;

public class ASUserNotFoundException extends RuntimeException {
    public ASUserNotFoundException(String clientId, String unionId) {
        super(String.format("Can not found User in AS, clientId:%s ,unionId:%s .", clientId, unionId));
    }
}

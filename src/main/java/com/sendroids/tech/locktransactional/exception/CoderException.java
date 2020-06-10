package com.sendroids.tech.locktransactional.exception;

abstract class CoderException extends RuntimeException {

    CoderException(String msg) {
        super(msg);
    }
}

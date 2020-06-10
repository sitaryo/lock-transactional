package com.sendroids.tech.locktransactional.exception;

public class CoderNotFoundException extends CoderException{

    public CoderNotFoundException() {
        super("coder not found");
    }
}

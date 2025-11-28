package com.pizzaro_go.common.dtos;

/**
 * Instead of returning a plain String, it's better to use a DTO because it allows adding new fields in the future,
 * and many systems also prefer receiving a structured JSON object.
 */
public class MessageResponse {
    private String message;

    public MessageResponse(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

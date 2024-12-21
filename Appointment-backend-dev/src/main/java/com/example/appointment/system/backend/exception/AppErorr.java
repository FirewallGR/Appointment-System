package com.example.appointment.system.backend.exception;

import lombok.Data;

import java.util.Date;

@Data
public class AppErorr extends RuntimeException {
    private int status;
    private String message;
    private String details;
    private Date timestamp;

    public AppErorr(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

    public AppErorr(int status, String message, String details) {
        super(message);
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = new Date();
    }
}

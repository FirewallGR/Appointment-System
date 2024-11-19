package com.example.appointment.system.backend.exception;

import lombok.Data;

import java.util.Date;

@Data
public class AppErorr {
    private int status;
    private String message;
    private Date timestamp;

    public AppErorr(int status, String message){
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}

package com.galimberti.paymentsapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) 
public class PaBadRequestException extends RuntimeException{

    public PaBadRequestException(String message){
        super(message);
    }

}

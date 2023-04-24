package com.test.exercise.marketprice.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException() {
        super();
    }

    public PriceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceNotFoundException(String message) {
        super(message);
    }

    public PriceNotFoundException(Throwable cause) {
        super(cause);
    }
}

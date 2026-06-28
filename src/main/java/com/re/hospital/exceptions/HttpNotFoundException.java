package com.re.hospital.exceptions;

import org.springframework.http.HttpStatus;

public class HttpNotFoundException extends AppException {
    public HttpNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
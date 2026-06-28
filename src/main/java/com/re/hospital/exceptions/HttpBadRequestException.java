package com.re.hospital.exceptions;

import org.springframework.http.HttpStatus;

public class HttpBadRequestException extends AppException {
    public HttpBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
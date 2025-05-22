package kr.end.backend.global.exception.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus httpStatus, String path ,String message) {

}
package kr.end.backend.global.exception;

import lombok.Getter;

@Getter

public class JJUSTException extends RuntimeException {

    private final ErrorCode errorCode;

    public JJUSTException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

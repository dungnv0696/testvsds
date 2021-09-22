package com.lifesup.gbtd.exception;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.util.Const;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private String message;
    private ErrorCode errorCode;
    private Object payload;
    private String[] args;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public ServerException(ErrorCode errorCode, String... args) {
        super(errorCode.toString());
        this.errorCode = errorCode;
        this.args = args;
    }

    public ServerException(String field, ErrorCode errorCode) {
        super(errorCode.toString());
        this.message = field + " " + errorCode.getMessage();
        this.errorCode = errorCode;
    }

    public ServerException(Object payload) {
        super(Const.ERROR);
        this.payload = payload;
    }
}

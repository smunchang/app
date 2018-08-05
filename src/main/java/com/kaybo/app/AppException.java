package com.kaybo.app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppException extends RuntimeException{


    private int errCode;
    public AppException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}

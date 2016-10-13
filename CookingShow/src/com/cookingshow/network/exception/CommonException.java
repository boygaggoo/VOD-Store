package com.cookingshow.network.exception;

@SuppressWarnings("serial")
public class CommonException extends Exception {

    public final int errorCode;

    public CommonException() {
        errorCode = 0;
    }

    public CommonException(String exceptionMessage) {
        super(exceptionMessage);
        errorCode = 0;
    }
    
    public CommonException(int errorCode, String exceptionMessage) {
        super(exceptionMessage);
        this.errorCode = errorCode;
    }

    public CommonException(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        errorCode = 0;
    }

    public CommonException(Throwable cause) {
        super(cause);
        errorCode = 0;
    }
}

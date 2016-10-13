package com.cookingshow.network.exception;

@SuppressWarnings("serial")
public final class TimeoutException extends CommonException {
    
    public TimeoutException() {
        super();
    }

    public TimeoutException(String errorMsg) {
        super(errorMsg);
    }

    public TimeoutException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}

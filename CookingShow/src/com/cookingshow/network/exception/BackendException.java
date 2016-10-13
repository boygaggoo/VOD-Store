package com.cookingshow.network.exception;

@SuppressWarnings("serial")
public final class BackendException extends CommonException {

    public BackendException() {
        super();
    }
    
    public BackendException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}

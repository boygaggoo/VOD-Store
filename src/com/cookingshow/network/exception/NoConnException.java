/**
 * 
 */
package com.cookingshow.network.exception;

@SuppressWarnings("serial")
public final class NoConnException extends CommonException {

    public NoConnException() {
        super();
    }
    
    public NoConnException(String msg) {
        super(msg);
    }
    
    public NoConnException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}

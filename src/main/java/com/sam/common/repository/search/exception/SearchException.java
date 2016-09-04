package com.sam.common.repository.search.exception;

import org.springframework.core.NestedRuntimeException;


public class SearchException extends NestedRuntimeException {

    public SearchException(String msg) {
        super(msg);
    }

    public SearchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

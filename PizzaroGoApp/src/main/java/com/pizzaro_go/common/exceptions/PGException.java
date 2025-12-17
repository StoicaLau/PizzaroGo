package com.pizzaro_go.common.exceptions;

/**
 * Standard exception for Pizzaro Go services
 */
public class PGException extends RuntimeException{

    /**
     * Constructs a new Pizzaro Go exception with the specified message
     *
     * @param message the exception message
     */
    public PGException(String message){ super(message);}

    /**
     * Constructs a new Pizzaro Go exception with the specified cause
     *
     * @param cause the exception cause
     */
    public PGException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new Pizzaro Go exception with the  specified message and specified exception
     *
     * @param message the exception  message
     * @param ex           the suppressed exception
     */
    public PGException(String message, Exception ex) {
        super(message, ex);
    }
    
}

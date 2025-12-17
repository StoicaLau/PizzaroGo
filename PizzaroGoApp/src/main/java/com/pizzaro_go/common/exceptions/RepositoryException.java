package com.pizzaro_go.common.exceptions;

/**
 * Repository exception
 */
public class RepositoryException extends PGException {

    /**
     * Constructor
     *
     * @param cause of exception
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message of exception
     */
    public RepositoryException(String message) {
        super(message);
    }

}

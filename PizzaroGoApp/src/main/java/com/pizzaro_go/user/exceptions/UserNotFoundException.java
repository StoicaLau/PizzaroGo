package com.pizzaro_go.user.exceptions;

import com.pizzaro_go.common.exceptions.PGException;

/**
 * Exception thrown when couldn't find any user
 */
public class UserNotFoundException extends PGException {

    /**
     * Constructor
     *
     * @param message the exception message
     */
    public UserNotFoundException(String message){super(message);}
}

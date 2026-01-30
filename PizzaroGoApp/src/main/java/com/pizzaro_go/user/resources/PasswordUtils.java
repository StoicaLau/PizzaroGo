package com.pizzaro_go.user.resources;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password encryption using BCrypt.
 */
public class PasswordUtils {

    /**
     * Encrypts a password using BCrypt.
     *
     * @param password the plain text password
     * @return the encrypted password
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a password against an encrypted password.
     *
     * @param rawPassword       the plain text password
     * @param encryptedPassword the encrypted password to check against
     * @return true if matches, false otherwise
     */
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (encryptedPassword == null || !encryptedPassword.startsWith("$2")) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }
}

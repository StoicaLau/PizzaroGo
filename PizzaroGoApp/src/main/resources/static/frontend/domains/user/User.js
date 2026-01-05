/**
 * User model matching the backend User entity.
 */
export class User {
    /**
     * @param {string} username
     * @param {string} email
     * @param {string} phone
     * @param {string} password
     * @param {string} [role]
     * @param {number} [id]
     */
    constructor(username, email, phone, password, role = 'CUSTOMER', id = null) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    /**
     * Creates a User instance from a JSON object.
     * @param {Object} json
     * @returns {User}
     */
    static fromJson(json) {
        return new User(
            json.username,
            json.email,
            json.phone,
            null, // Password is usually not returned from backend
            json.role,
            json.id
        );
    }
}

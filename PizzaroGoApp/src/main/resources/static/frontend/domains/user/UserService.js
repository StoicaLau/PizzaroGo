import { User } from './User.js';

/**
 * Service for handling User API interactions.
 */
export class UserService {

    constructor() {
        this.baseUrl = '/users';
    }

    /**
     * Registers a new user.
     * @param {User} user
     * @returns {Promise<any>}
     */
    async register(user) {
        const response = await fetch(`${this.baseUrl}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Registration failed' }));
            throw new Error(error.message || 'Registration failed');
        }

        return await response.json();
    }

    /**
     * Retrieves all users.
     * @returns {Promise<User[]>}
     */
    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        const data = await response.json();
        return data.map(item => User.fromJson(item));
    }

    /**
     * Logs in a user.
     * @param {string} emailOrUsername
     * @param {string} password
     * @returns {Promise<User>}
     */
    async login(email, password) {
        // Backend now uses UserRequest for login, which contains email and password fields.
        // We construct a partial UserRequest object.
        const loginData = {
            email: email,
            password: password
            // username, phone, etc. are null/undefined which is fine for UserRequest
        };

        const response = await fetch(`${this.baseUrl}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Login failed' }));
            throw new Error(error.message || 'Login failed');
        }

        const userData = await response.json();
        return User.fromJson(userData);
    }
}

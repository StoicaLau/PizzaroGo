import { Order } from './Order.js';

class OrderService {
    constructor() {
        this.baseUrl = '/api/orders';
    }

    /**
     * Creates a new order.
     * @param {Object} orderData 
     * @returns {Promise<Object>}
     */
    async create(orderData) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create order');
        }
        return await response.json();
    }

    /**
     * Gets all orders for a specific user.
     * @param {number} userId 
     * @returns {Promise<Order[]>}
     */
    async getByUserId(userId) {
        const response = await fetch(`${this.baseUrl}/user/${userId}`);
        if (!response.ok) throw new Error('Failed to fetch orders');
        const data = await response.json();
        return data.map(item => Order.fromJson(item));
    }

    /**
     * Gets an order by ID.
     * @param {number} id 
     * @returns {Promise<Order>}
     */
    async getById(id) {
        const response = await fetch(`${this.baseUrl}/${id}`);
        if (!response.ok) throw new Error('Failed to fetch order');
        const data = await response.json();
        return Order.fromJson(data);
    }
}

export const orderService = new OrderService();

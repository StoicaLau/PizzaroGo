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
        const response = await fetch(`${this.baseUrl}/byUserId/${userId}`);
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

    /**
     * Updates an existing order.
     * @param {Object} orderData 
     * @returns {Promise<Object>}
     */
    async update(orderData) {
        const response = await fetch(this.baseUrl, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update order');
        }
        return await response.json();
    }

    /**
     * Deletes an order by ID.
     * @param {number} id 
     * @returns {Promise<Object>}
     */
    async deleteById(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Failed to delete order');
        return await response.json();
    }

    /**
     * Gets all active orders (PENDING or PROCESSING).
     * @returns {Promise<Order[]>}
     */
    async getActiveOrders() {
        const response = await fetch(`${this.baseUrl}/active`);
        if (!response.ok) throw new Error('Failed to fetch active orders');
        const data = await response.json();
        console.log("OrderService.getActiveOrders raw data:", data);
        return data.map(item => Order.fromJson(item));
    }

    /**
     * Updates the status of an existing order.
     * @param {Object} orderStatusData {id, status, estimatedAt}
     * @returns {Promise<Order>}
     */
    async updateStatus(orderStatusData) {
        const response = await fetch(`${this.baseUrl}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderStatusData)
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update order status');
        }
        const data = await response.json();
        return Order.fromJson(data);
    }
}

export const orderService = new OrderService();


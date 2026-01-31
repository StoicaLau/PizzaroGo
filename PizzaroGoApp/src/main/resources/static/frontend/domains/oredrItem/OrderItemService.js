
class OrderItemService {
    constructor() {
        this.baseUrl = '/api/order_items';
    }

    /**
     * Updates the status of an order item.
     * @param {number} itemId 
     * @param {string} status 
     * @returns {Promise<Object>}
     */
    async updateStatus(itemId, status) {
        const response = await fetch(`${this.baseUrl}/${itemId}/status?status=${status}`, {
            method: 'PATCH'
        });
        if (!response.ok) {
            throw new Error('Failed to update order item status');
        }
        return await response.json();
    }
}

export const orderItemService = new OrderItemService();

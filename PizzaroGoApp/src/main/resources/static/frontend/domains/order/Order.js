import { OrderItem } from '../oredrItem/OrderItem.js';

/**
 * Order model matching the backend Order entity.
 */
export class Order {
    /**
     * @param {number} [id]
     * @param {number} userId
     * @param {string} [createdAt]
     * @param {string} [estimatedAt]
     * @param {string} [status]
     * @param {number} [orderPrice]
     * @param {number} [deliveryPrice]
     * @param {number} [totalPrice]
     * @param {OrderItem[]} [orderItems]
     */
    constructor(id = null, userId, createdAt = null, estimatedAt = null, status = 'PENDING', orderPrice = 0, deliveryPrice = 0, totalPrice = 0, orderItems = []) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.estimatedAt = estimatedAt;
        this.status = status;
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
    }

    /**
     * Creates an Order instance from a JSON object.
     * @param {Object} json
     * @returns {Order}
     */
    static fromJson(json) {
        console.log("Order.fromJson mapping:", json);
        try {
            const orderItems = json.orderItems ? json.orderItems.map(item => OrderItem.fromJson(item)) : [];
            return new Order(
                json.id,
                json.userId,
                json.createdAt,
                json.estimatedAt,
                json.status,
                json.orderPrice,
                json.deliveryPrice,
                json.totalPrice,
                orderItems
            );
        } catch (e) {
            console.error("Order.fromJson error:", e, json);
            throw e;
        }
    }
}

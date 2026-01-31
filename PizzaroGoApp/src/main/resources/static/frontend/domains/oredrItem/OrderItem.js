/**
 * OrderItem model matching the backend OrderItem entity.
 */
export class OrderItem {
    /**
     * @param {number} [id]
     * @param {number} orderId
     * @param {number} menuProductId
     * @param {number} quantity
     * @param {number} totalPrice
     * @param {string} [menuProductName]
     * @param {string} [menuProductDescription]
     * @param {string} [status]
     */
    constructor(id = null, orderId, menuProductId, quantity, totalPrice, menuProductName = null, menuProductDescription = null, status = 'PENDING') {
        this.id = id;
        this.orderId = orderId;
        this.menuProductId = menuProductId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.menuProductName = menuProductName;
        this.menuProductDescription = menuProductDescription;
        this.status = status;
    }

    /**
     * Creates an OrderItem instance from a JSON object.
     * @param {Object} json
     * @returns {OrderItem}
     */
    static fromJson(json) {
        return new OrderItem(
            json.id,
            json.orderId,
            json.menuProductId,
            json.quantity,
            json.totalPrice,
            json.menuProductName,
            json.menuProductDescription,
            json.status
        );
    }
}

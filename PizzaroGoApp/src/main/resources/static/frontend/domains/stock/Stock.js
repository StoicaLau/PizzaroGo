/**
 * Represents a Stock item in the frontend domain.
 */
export class Stock {
    /**
     * @param {number|null} id
     * @param {string} name
     * @param {number} quantity
     * @param {string} unit
     */
    constructor(id = null, name = '', quantity = 0, unit = 'KG') {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Creates a Stock instance from a JSON object.
     * @param {Object} json
     * @returns {Stock}
     */
    static fromUrl(json) {
        return new Stock(json.id, json.name, json.quantity, json.unit);
    }
}

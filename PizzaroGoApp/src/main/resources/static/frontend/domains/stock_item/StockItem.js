/**
 * Represents a Stock item in the frontend domain.
 */
export class StockItem {
    /**
     * @param {number|null} id
     * @param {string} name
     * @param {string} category
     * @param {number} quantity
     * @param {string} unit
     */
    constructor(id = null, name = '', category = 'INGREDIENT', quantity = 0, unit = 'KG') {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Creates a StockItem instance from a JSON object.
     * @param {Object} json
     * @returns {StockItem}
     */
    static fromUrl(json) {
        return new StockItem(json.id, json.name, json.category, json.quantity, json.unit);
    }
}

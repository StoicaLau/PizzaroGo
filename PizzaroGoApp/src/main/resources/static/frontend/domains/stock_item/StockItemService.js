import { StockItem } from './StockItem.js';

/**
 * Service for managing stock item operations via API.
 */
class StockItemService {
    constructor() {
        this.baseUrl = '/api/stock_items';
    }

    /**
     * Fetches all stock items.
     * @returns {Promise<Array<StockItem>>} List of stock items.
     */
    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch stocks');
        }
        const data = await response.json();
        return data.map(item => StockItem.fromUrl(item));
    }

    /**
     * Fetches stock items by product category.
     * @param {string} productCategory The product category to filter by (e.g., 'PIZZA').
     * @returns {Promise<Array<StockItem>>} List of stock items.
     */
    async getByProductCategory(productCategory) {
        const response = await fetch(`${this.baseUrl}/by-product-category/${productCategory}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch stocks for category ${productCategory}`);
        }
        const data = await response.json();
        return data.map(item => StockItem.fromUrl(item));
    }

    /**
     * Creates a new stock item.
     * @param {Object} stockItem The stock item object to create.
     * @returns {Promise<Object>} The created stock item.
     */
    async create(stockItem) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(stockItem)
        });
        if (!response.ok) {
            throw new Error('Failed to add stock item');
        }
        return await response.json();
    }

    /**
     * Updates an existing stock item.
     * @param {number} id The ID of the stock item.
     * @param {Object} stockItem The updated stock item data.
     * @returns {Promise<Object>} The updated stock item.
     */
    async update(id, stockItem) {
        // The backend PatchMapping expects the ID inside the body for StockItemRequest
        const payload = { ...stockItem, id: parseInt(id) };
        const response = await fetch(this.baseUrl, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            throw new Error('Failed to update stock item');
        }
        return await response.json();
    }

    /**
     * Deletes a stock item.
     * @param {number} id The ID of the stock item to delete.
     */
    async delete(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete stock item');
        }
    }

    /**
     * Deletes all stock items.
     */
    async deleteAll() {
        const response = await fetch(this.baseUrl, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete all stocks');
        }
        return await response.json();
    }

    /**
     * Imports stocks from a file.
     * @param {File} file The Excel file to import.
     * @returns {Promise<Object>} The server response.
     */
    async importStockItems(file) {
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(`${this.baseUrl}/import`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Import failed');
        }
        return await response.json();
    }

    /**
     * Triggers the export of stocks (browser download).
     */
    exportStockItems() {
        window.location.href = `${this.baseUrl}/export`;
    }
}

// Export a singleton instance
export const stockItemService = new StockItemService();

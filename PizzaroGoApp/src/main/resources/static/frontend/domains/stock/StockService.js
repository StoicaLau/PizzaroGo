import { Stock } from './Stock.js';

/**
 * Service for managing stock operations via API.
 */
class StockService {
    constructor() {
        this.baseUrl = '/stocks';
    }

    /**
     * Fetches all stock items.
     * @returns {Promise<Array<Stock>>} List of stock items.
     */
    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch stocks');
        }
        const data = await response.json();
        return data.map(item => Stock.fromUrl(item));
    }

    /**
     * Adds a new stock item.
     * @param {Object} stock The stock object to add.
     * @returns {Promise<Object>} The added stock item.
     */
    async add(stock) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(stock)
        });
        if (!response.ok) {
            throw new Error('Failed to add stock');
        }
        return await response.json();
    }

    /**
     * Updates an existing stock item.
     * @param {number} id The ID of the stock item.
     * @param {Object} stock The updated stock data.
     * @returns {Promise<Object>} The updated stock item.
     */
    async update(id, stock) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(stock)
        });
        if (!response.ok) {
            throw new Error('Failed to update stock');
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
            throw new Error('Failed to delete stock');
        }
    }

    /**
     * Imports stocks from a file.
     * @param {File} file The Excel file to import.
     * @returns {Promise<Object>} The server response.
     */
    async importStocks(file) {
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
     * Triggers the export of stocks (not implemented in backend yet, but prepared here).
     */
    async exportStocks() {
        // Assuming backend will support GET /stocks/export to download file
        window.location.href = `${this.baseUrl}/export`;
    }
}

// Export a singleton instance
export const stockService = new StockService();

import { MenuProduct } from './MenuProduct.js';

class MenuProductService {
    constructor() {
        this.baseUrl = '/api/menu_products';
    }

    async getAvailable(all = false) {
        try {
            const url = all ? `${this.baseUrl}?all=true` : this.baseUrl;
            const response = await fetch(url, {
                cache: 'no-store', // Ensure we get fresh data from server (stock levels)
                headers: {
                    'pragma': 'no-cache',
                    'cache-control': 'no-cache'
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to fetch available menu products');
            }

            const data = await response.json();
            return data.map(item => MenuProduct.fromResponse(item));
        } catch (error) {
            console.error("MenuProductService.getAvailable error:", error);
            throw error;
        }
    }

    async create(productData) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = 'Operation failed';
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorText;
            } catch (e) {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    }

    async update(id, productData) {
        const response = await fetch(`${this.baseUrl}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ...productData, id: parseInt(id) })
        });
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = 'Operation failed';
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorText;
            } catch (e) {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    }

    async delete(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = 'Operation failed';
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorText;
            } catch (e) {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    }

    async deleteAll() {
        const response = await fetch(this.baseUrl, {
            method: 'DELETE'
        });
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = 'Operation failed';
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorText;
            } catch (e) {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    }

    exportProducts() {
        window.location.href = `${this.baseUrl}/export`;
    }

    async importProducts(file) {
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(`${this.baseUrl}/import`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = 'Operation failed';
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorText;
            } catch (e) {
                errorMessage = errorText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        return await response.json();
    }
}

export const menuProductService = new MenuProductService();

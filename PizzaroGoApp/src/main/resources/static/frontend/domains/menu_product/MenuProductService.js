import { MenuProduct } from './MenuProduct.js';

class MenuProductService {
    constructor() {
        this.baseUrl = '/api/menu_products';
    }

    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) throw new Error('Failed to fetch menu products');
        const data = await response.json();
        return data.map(item => MenuProduct.fromResponse(item));
    }

    async create(productData) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productData)
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create product');
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
            const error = await response.json();
            throw new Error(error.message || 'Failed to update product');
        }
        return await response.json();
    }

    async delete(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete product');
        }
        return await response.json();
    }

    async deleteAll() {
        const response = await fetch(this.baseUrl, {
            method: 'DELETE'
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete all products');
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
            const error = await response.json();
            throw new Error(error.message || 'Failed to import products');
        }
        return await response.json();
    }
}

export const menuProductService = new MenuProductService();

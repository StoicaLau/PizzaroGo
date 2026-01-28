import { ProductStockUsage } from '../product_stock_usage/ProductStockUsage.js';

export class MenuProduct {
    constructor(id = null, name = '', imageURL = '', description = '', productCategory = 'PIZZA', price = 0.0, stockUsages = []) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.description = description;
        this.productCategory = productCategory;
        this.price = price;
        this.stockUsages = stockUsages.map(u =>
            u instanceof ProductStockUsage ? u : ProductStockUsage.fromResponse(u)
        );
    }

    static fromResponse(data) {
        return new MenuProduct(
            data.id,
            data.name,
            data.imageURL,
            data.description, // description is in MenuProductResponse.java
            data.productCategory,
            data.price,
            data.stockUsages || []
        );
    }

    toRequest() {
        // Matches MenuProductRequest.java (NO description)
        return {
            id: this.id || null,
            name: this.name,
            imageURL: this.imageURL,
            description: this.description,
            productCategory: this.productCategory,
            price: this.price,
            stockUsages: this.stockUsages.map(usage => usage.toRequest())
        };
    }
}

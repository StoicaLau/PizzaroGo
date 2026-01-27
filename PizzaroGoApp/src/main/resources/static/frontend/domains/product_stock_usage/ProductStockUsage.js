export class ProductStockUsage {
    constructor(id = null, menuProductId = null, stockItemId = null, quantityPerUnit = 1.0) {
        this.id = id;
        this.menuProductId = menuProductId;
        this.stockItemId = stockItemId;
        this.quantityPerUnit = quantityPerUnit;
    }

    static fromResponse(data) {
        return new ProductStockUsage(
            data.id,
            data.menuProductId,
            data.stockItemId,
            data.quantityPerUnit
        );
    }

    toRequest() {
        return {
            id: this.id || null,
            menuProductId: this.menuProductId || null,
            stockItemId: this.stockItemId,
            quantityPerUnit: this.quantityPerUnit
        };
    }
}

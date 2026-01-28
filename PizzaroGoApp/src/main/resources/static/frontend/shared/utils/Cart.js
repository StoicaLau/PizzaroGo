/**
 * Simple Cart utility for managing order items in localStorage.
 */
export class Cart {
    static STORAGE_KEY = 'pizzarogo_cart';

    static getItems() {
        const cart = localStorage.getItem(this.STORAGE_KEY);
        return cart ? JSON.parse(cart) : [];
    }

    static saveItems(items) {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(items));
        window.dispatchEvent(new CustomEvent('cart-change', { detail: items }));
    }

    static addItem(product, quantity = 1) {
        const items = this.getItems();
        const existingItem = items.find(item => item.menuProductId === product.id);

        if (existingItem) {
            existingItem.quantity += quantity;
            existingItem.totalPrice = existingItem.quantity * product.price;
        } else {
            items.push({
                menuProductId: product.id,
                menuProductName: product.name,
                price: product.price,
                quantity: quantity,
                totalPrice: quantity * product.price,
                imageURL: product.imageURL
            });
        }

        this.saveItems(items);
    }

    static removeItem(productId) {
        let items = this.getItems();
        items = items.filter(item => item.menuProductId !== productId);
        this.saveItems(items);
    }

    static updateQuantity(productId, quantity) {
        const items = this.getItems();
        const item = items.find(item => item.menuProductId === productId);
        if (item) {
            item.quantity = Math.max(1, quantity);
            item.totalPrice = item.quantity * item.price;
            this.saveItems(items);
        }
    }

    static clear() {
        this.saveItems([]);
    }

    static getTotal() {
        const items = this.getItems();
        return items.reduce((sum, item) => sum + item.totalPrice, 0);
    }

    static getCount() {
        const items = this.getItems();
        return items.reduce((sum, item) => sum + item.quantity, 0);
    }
}

import { menuProductService } from '../../../domains/menu_product/MenuProductService.js';
import { stockItemService } from '../../../domains/stock_item/StockItemService.js';
import { MenuProduct } from '../../../domains/menu_product/MenuProduct.js';
import { ProductStockUsage } from '../../../domains/product_stock_usage/ProductStockUsage.js';

export class ProductManager {
    constructor() {
        this.products = [];
        this.stockItems = [];
        this.selectedIngredients = []; // List of ProductStockUsage objects
        this.currentProductId = null;
        this.searchQuery = '';
        this.stockSearchQuery = '';
        this.currentSort = {
            field: 'id',
            order: 'asc'
        };
        this.init();
    }

    async init() {
        this.bindEvents();
        await this.loadProducts();
    }

    bindEvents() {
        // Toolbar
        document.getElementById('product-search')?.addEventListener('input', (e) => {
            this.searchQuery = e.target.value.toLowerCase();
            this.renderTable();
        });

        document.getElementById('btn-add-product')?.addEventListener('click', () => this.openModal());
        document.getElementById('btn-import-products')?.addEventListener('click', () => {
            document.getElementById('product-import-input')?.click();
        });
        document.getElementById('product-import-input')?.addEventListener('change', (e) => this.handleImport(e));
        document.getElementById('btn-export-products')?.addEventListener('click', () => menuProductService.exportProducts());
        document.getElementById('btn-delete-all-products')?.addEventListener('click', () => this.openConfirmModal(null, true));

        // Modal Basics
        document.querySelectorAll('.close-modal, .close-modal-btn').forEach(btn => {
            btn.addEventListener('click', () => this.closeModal());
        });

        // Step Navigation
        document.getElementById('btn-next-step')?.addEventListener('click', () => this.goToStep(2));
        document.getElementById('btn-prev-step')?.addEventListener('click', () => this.goToStep(1));
        document.getElementById('btn-save-product')?.addEventListener('click', () => this.handleSave());

        // Ingredient Search
        document.getElementById('stock-item-search')?.addEventListener('input', (e) => {
            this.stockSearchQuery = e.target.value.toLowerCase();
            this.renderStockList();
        });

        // Global Confirmation Modal
        document.getElementById('confirm-cancel-prod')?.addEventListener('click', () => this.closeConfirmModal());
        document.getElementById('confirm-yes-prod')?.addEventListener('click', () => this.processDelete());

        // Table Sorting
        document.querySelectorAll('.product-table th.sortable').forEach(th => {
            th.addEventListener('click', () => this.handleSort(th.dataset.field));
        });
    }

    handleSort(field) {
        if (this.currentSort.field === field) {
            this.currentSort.order = this.currentSort.order === 'asc' ? 'desc' : 'asc';
        } else {
            this.currentSort.field = field;
            this.currentSort.order = 'asc';
        }

        this.updateSortIcons();
        this.applySort();
        this.renderTable();
    }

    applySort() {
        const { field, order } = this.currentSort;
        this.products.sort((a, b) => {
            let valA = a[field];
            let valB = b[field];

            if (valA === null || valA === undefined) valA = '';
            if (valB === null || valB === undefined) valB = '';

            if (field === 'id' || field === 'price') {
                return order === 'asc' ? valA - valB : valB - valA;
            }

            valA = valA.toString().toLowerCase();
            valB = valB.toString().toLowerCase();

            if (valA < valB) return order === 'asc' ? -1 : 1;
            if (valA > valB) return order === 'asc' ? 1 : -1;
            return 0;
        });
    }

    updateSortIcons() {
        document.querySelectorAll('.product-table th.sortable i').forEach(icon => {
            icon.className = 'fas fa-sort';
            icon.style.opacity = '0.5';
        });

        const activeTh = document.querySelector(`.product-table th.sortable[data-field="${this.currentSort.field}"]`);
        if (activeTh) {
            const icon = activeTh.querySelector('i');
            icon.className = this.currentSort.order === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
            icon.style.opacity = '1';
        }
    }

    async loadProducts() {
        try {
            this.products = await menuProductService.getAll();
            this.updateStats();
            this.applySort();
            this.renderTable();
        } catch (e) {
            this.showToast('error', 'Failed to load products');
        }
    }

    updateStats() {
        const total = this.products.length;
        const drinks = this.products.filter(p => (p.productCategory || '').toUpperCase() === 'DRINK').length;
        const sauces = this.products.filter(p => (p.productCategory || '').toUpperCase() === 'SAUCE').length;

        const totalEl = document.getElementById('stat-total-products');
        const drinksEl = document.getElementById('stat-total-drinks');
        const saucesEl = document.getElementById('stat-total-sauces');

        if (totalEl) totalEl.textContent = total;
        if (drinksEl) drinksEl.textContent = drinks;
        if (saucesEl) saucesEl.textContent = sauces;
    }


    renderTable() {
        const tbody = document.getElementById('product-table-body');
        if (!tbody) return;
        tbody.innerHTML = '';

        const filtered = this.products.filter(p => p.name.toLowerCase().includes(this.searchQuery) || p.productCategory.toLowerCase().includes(this.searchQuery));

        filtered.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${p.id}</td>
                <td><img src="${p.imageURL || '/assets/no-pizza.png'}" class="product-img-cell" onerror="this.src='/assets/no-pizza.png'"></td>
                <td>${p.name}</td>
                <td><span class="category-pill">${p.productCategory}</span></td>
                <td>${p.price.toFixed(2)} RON</td>
                <td style="max-width: 250px; font-size: 0.85rem; color: #aaa;">${p.description || '-'}</td>
                <td>
                    <button class="action-btn edit-btn" data-id="${p.id}"><i class="fas fa-pencil-alt"></i></button>
                    <button class="action-btn delete-btn" data-id="${p.id}"><i class="fas fa-trash"></i></button>
                </td>
            `;

            tr.querySelector('.edit-btn').addEventListener('click', () => this.openModal(p));
            tr.querySelector('.delete-btn').addEventListener('click', () => this.openConfirmModal(p.id));
            tbody.appendChild(tr);
        });
    }

    renderStockList() {
        const container = document.getElementById('available-stock-list');
        if (!container) return;
        container.innerHTML = '';

        const filtered = this.stockItems.filter(s => s.name.toLowerCase().includes(this.stockSearchQuery));

        filtered.forEach(s => {
            const div = document.createElement('div');
            div.className = 'ingredient-item';
            div.draggable = true;
            div.innerHTML = `
                <div class="ing-info">
                    <span class="ing-name">${s.name}</span>
                    <span class="ing-meta">${s.category} | ${s.quantity} ${s.unit} available</span>
                </div>
                <i class="fas fa-plus-circle" style="color: #4caf50;"></i>
            `;

            div.addEventListener('click', () => this.addIngredient(s));
            div.addEventListener('dragstart', (e) => {
                e.dataTransfer.setData('stockItem', JSON.stringify(s));
            });
            container.appendChild(div);
        });
    }

    addIngredient(stockItem) {
        if (this.selectedIngredients.find(i => i.stockItemId === stockItem.id)) {
            this.showToast('warning', 'Ingredient already added');
            return;
        }

        this.selectedIngredients.push(new ProductStockUsage(
            null,
            this.currentProductId,
            stockItem.id,
            1.0
        ));
        this.renderSelectedIngredients();
    }

    removeIngredient(stockItemId) {
        this.selectedIngredients = this.selectedIngredients.filter(i => i.stockItemId !== stockItemId);
        this.renderSelectedIngredients();
    }

    renderSelectedIngredients() {
        const container = document.getElementById('selected-ingredients-list');
        if (!container) return;
        container.innerHTML = '';

        this.selectedIngredients.forEach(ing => {
            const stock = this.stockItems.find(s => s.id === ing.stockItemId);
            const div = document.createElement('div');
            div.className = 'ingredient-item';
            div.innerHTML = `
                <div class="ing-info">
                    <span class="ing-name">${stock ? stock.name : 'Unknown Ingredient'}</span>
                </div>
                <div class="ing-controls">
                    <input type="number" class="qty-input" value="${ing.quantityPerUnit}" step="0.1" min="0.1" data-id="${ing.stockItemId}">
                    <span>${stock ? stock.unit : ''}</span>
                    <i class="fas fa-times remove-ing" data-id="${ing.stockItemId}"></i>
                </div>
            `;

            div.querySelector('.qty-input').addEventListener('change', (e) => {
                ing.quantityPerUnit = parseFloat(e.target.value);
            });

            div.querySelector('.remove-ing').addEventListener('click', () => this.removeIngredient(ing.stockItemId));
            container.appendChild(div);
        });

        // Setup drop zone
        container.addEventListener('dragover', (e) => e.preventDefault());
        container.addEventListener('drop', (e) => {
            const data = e.dataTransfer.getData('stockItem');
            if (data) this.addIngredient(JSON.parse(data));
        });
    }

    openModal(product = null) {
        const modal = document.getElementById('product-modal');
        const title = document.getElementById('modal-title');
        this.currentProductId = product ? product.id : null;
        this.selectedIngredients = [];

        if (product) {
            title.textContent = 'Edit Product';
            document.getElementById('product-name').value = product.name;
            document.getElementById('product-image').value = product.imageURL;
            document.getElementById('product-category').value = (product.productCategory || '').toUpperCase();
            document.getElementById('product-price').value = product.price;

            // Map existing stock usages
            if (product.stockUsages) {
                this.selectedIngredients = product.stockUsages.map(usage => {
                    return new ProductStockUsage(
                        usage.id,
                        usage.menuProductId,
                        usage.stockItemId,
                        usage.quantityPerUnit
                    );
                });
            }
        } else {
            title.textContent = 'Add Product';
            document.getElementById('product-form-step-1').reset();
        }

        this.goToStep(1);
        modal.classList.remove('hidden');
    }

    closeModal() {
        document.getElementById('product-modal').classList.add('hidden');
    }

    async goToStep(step) {
        document.getElementById('step-1').classList.toggle('hidden', step !== 1);
        document.getElementById('step-2').classList.toggle('hidden', step !== 2);

        // Update visual indicators
        document.querySelectorAll('.step-indicator').forEach(el => {
            el.classList.toggle('active', parseInt(el.dataset.step) === step);
        });

        if (step === 2) {
            const productCategory = document.getElementById('product-category').value;
            try {
                this.stockItems = await stockItemService.getByProductCategory(productCategory);
                this.renderStockList();
                this.renderSelectedIngredients();
            } catch (e) {
                this.showToast('error', 'Failed to load appropriate stock items');
            }
        }
    }

    async handleSave() {
        const id = this.currentProductId;
        const name = document.getElementById('product-name').value;
        const imageURL = document.getElementById('product-image').value;
        const productCategory = document.getElementById('product-category').value;
        const price = parseFloat(document.getElementById('product-price').value);

        const existingProduct = this.products.find(p => p.id === id);
        const menuProduct = new MenuProduct(
            id,
            name,
            imageURL,
            existingProduct ? existingProduct.description : '',
            productCategory,
            price,
            this.selectedIngredients
        );

        const requestData = menuProduct.toRequest();

        try {
            if (id) {
                await menuProductService.update(id, requestData);
                this.showToast('success', 'Product updated!');
            } else {
                await menuProductService.create(requestData);
                this.showToast('success', 'Product created!');
            }
            this.closeModal();
            await this.loadProducts();
        } catch (e) {
            this.showToast('error', e.message);
        }
    }

    openConfirmModal(id, all = false) {
        this.pendingDeleteId = id;
        this.isDeletingAll = all;
        const modal = document.getElementById('confirm-modal-prod');
        const title = document.getElementById('confirm-title-prod');
        const msg = document.getElementById('confirm-message-prod');

        title.textContent = all ? 'Delete All Products?' : 'Delete Product?';
        msg.textContent = all
            ? 'Are you SURE you want to delete EVERY product? This is permanent!'
            : 'Are you sure you want to delete this product?';

        modal.classList.remove('hidden');
    }

    closeConfirmModal() {
        document.getElementById('confirm-modal-prod').classList.add('hidden');
    }

    async processDelete() {
        try {
            if (this.isDeletingAll) {
                await menuProductService.deleteAll();
            } else {
                await menuProductService.delete(this.pendingDeleteId);
            }
            this.showToast('success', 'Delete successful');
            await this.loadProducts();
        } catch (e) {
            this.showToast('error', e.message);
        } finally {
            this.closeConfirmModal();
        }
    }

    async handleImport(event) {
        const file = event.target.files[0];
        if (!file) return;

        try {
            this.showToast('info', 'Importing products...');
            await menuProductService.importProducts(file);
            this.showToast('success', 'Products imported successfully');
            await this.loadProducts();
        } catch (e) {
            console.error('Import error:', e);
            this.showToast('error', e.message);
        } finally {
            event.target.value = ''; // Reset input
        }
    }

    showToast(type, message) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.style.position = 'fixed';
            container.style.bottom = '20px';
            container.style.right = '20px';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        toast.style.padding = '1rem 2rem';
        toast.style.background = type === 'success' ? '#4caf50' : '#f44336';
        toast.style.color = 'white';
        toast.style.borderRadius = '10px';
        toast.style.marginTop = '10px';
        toast.style.boxShadow = '0 10px 20px rgba(0,0,0,0.2)';

        container.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    }
}

export function init() {
    new ProductManager();
}

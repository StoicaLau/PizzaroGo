import { stockItemService } from '../../../domains/stock_item/StockItemService.js';

export class StockPage {
    constructor() {
        this.pendingDeleteId = null;
        this.isDeletingAll = false;
        this.stocks = []; // Memory storage
        this.searchQuery = '';
        this.currentSort = {
            field: 'id',
            order: 'asc'
        };
        this.init();
    }

    async init() {
        this.bindEvents();
        await this.loadStocks();
    }

    bindEvents() {
        // Search Input
        const searchInput = document.getElementById('stock-search');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchQuery = e.target.value.toLowerCase();
                this.renderTable();
            });
        }

        // Import Button
        document.getElementById('file-input').addEventListener('change', (e) => this.handleImport(e));
        document.getElementById('btn-import').addEventListener('click', () => {
            document.getElementById('file-input').click();
        });

        // Export Button
        document.getElementById('btn-export').addEventListener('click', () => this.handleExport());

        // Delete All Button
        const btnDeleteAll = document.getElementById('btn-delete-all');
        if (btnDeleteAll) {
            btnDeleteAll.addEventListener('click', () => this.openConfirmModal(null, true));
        }

        // Add Stock Button
        document.getElementById('btn-add-stock').addEventListener('click', () => this.openModal());

        // Modal Close (General)
        document.querySelectorAll('.close-modal').forEach(btn => {
            btn.addEventListener('click', () => this.closeModals());
        });
        document.querySelectorAll('.close-modal-btn').forEach(btn => {
            btn.addEventListener('click', () => this.closeModals());
        });

        // Form Submit
        document.getElementById('stock-form').addEventListener('submit', (e) => this.handleFormSubmit(e));

        // Confirmation Modal Events
        document.getElementById('confirm-cancel').addEventListener('click', () => this.closeConfirmModal());
        document.getElementById('confirm-yes').addEventListener('click', () => this.processDelete());

        // Table Sorting
        document.querySelectorAll('.sortable').forEach(th => {
            th.addEventListener('click', () => this.handleSort(th.dataset.field));
        });
    }

    async loadStocks() {
        try {
            this.stocks = await stockItemService.getAll();
            this.applySort();
            this.renderTable();
        } catch (error) {
            console.error('Error loading stocks:', error);
            this.showToast('error', 'Failed to load stocks.');
        }
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
        this.stocks.sort((a, b) => {
            let valA = a[field];
            let valB = b[field];

            if (valA === null || valA === undefined) valA = '';
            if (valB === null || valB === undefined) valB = '';

            if (field === 'id' || field === 'quantity') {
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
        document.querySelectorAll('.sortable i').forEach(icon => {
            icon.className = 'fas fa-sort';
            icon.style.opacity = '0.5';
        });

        const activeTh = document.querySelector(`.sortable[data-field="${this.currentSort.field}"]`);
        if (activeTh) {
            const icon = activeTh.querySelector('i');
            icon.className = this.currentSort.order === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
            icon.style.opacity = '1';
        }
    }

    getFilteredStocks() {
        if (!this.searchQuery) return this.stocks;

        return this.stocks.filter(stock => {
            const name = (stock.name || '').toLowerCase();
            const category = (stock.category || '').toLowerCase();
            const id = (stock.id || '').toString();

            return name.includes(this.searchQuery) ||
                category.includes(this.searchQuery) ||
                id.includes(this.searchQuery);
        });
    }

    renderTable() {
        const tbody = document.getElementById('stock-table-body');
        tbody.innerHTML = '';

        const filteredStocks = this.getFilteredStocks();

        if (filteredStocks.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #eee; padding: 3rem; font-size: 1.2rem; font-weight: 700; background: rgba(0,0,0,0.2);">No items found matching your search.</td></tr>`;
            return;
        }

        filteredStocks.forEach(stock => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${stock.id}</td>
                <td>${stock.name}</td>
                <td>${stock.category}</td>
                <td>${stock.quantity}</td>
                <td>${stock.unit}</td>
                <td>
                    <button class="action-btn edit-btn" data-id="${stock.id}"><i class="fas fa-pencil-alt"></i></button>
                    <button class="action-btn delete-btn" data-id="${stock.id}"><i class="fas fa-trash"></i></button>
                </td>
            `;

            tr.querySelector('.edit-btn').addEventListener('click', () => this.openModal(stock));
            tr.querySelector('.delete-btn').addEventListener('click', () => this.openConfirmModal(stock.id));

            tbody.appendChild(tr);
        });
    }

    // Rest of the methods follow...
    async handleImport(event) {
        const file = event.target.files[0];
        if (!file) return;
        try {
            const response = await stockItemService.importStockItems(file);
            this.showToast('success', response.message || 'Import successful!');
            await this.loadStocks();
        } catch (error) {
            this.showToast('error', 'Import failed: ' + error.message);
        } finally {
            event.target.value = '';
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
            container.style.zIndex = '2000';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.textContent = message;
        toast.style.padding = '1rem 1.5rem';
        toast.style.marginBottom = '10px';
        toast.style.borderRadius = '8px';
        toast.style.color = '#fff';
        toast.style.fontWeight = 'bold';
        toast.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
        toast.style.animation = 'fadeIn 0.5s, fadeOut 0.5s 2.5s';

        if (type === 'success') {
            toast.style.backgroundColor = '#4caf50';
        } else {
            toast.style.backgroundColor = '#f44336';
        }

        container.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    }

    handleExport() {
        try {
            stockItemService.exportStockItems();
        } catch (error) {
            this.showToast('error', 'Export failed: ' + error.message);
        }
    }

    openConfirmModal(id, all = false) {
        this.pendingDeleteId = id;
        this.isDeletingAll = all;
        const modal = document.getElementById('confirm-modal');
        const title = document.getElementById('confirm-title');
        const message = document.getElementById('confirm-message');
        if (all) {
            title.textContent = 'Delete Everything?';
            message.textContent = 'Are you SURE you want to delete ALL stock items? This action cannot be undone!';
        } else {
            title.textContent = 'Delete Item?';
            message.textContent = 'Are you sure you want to delete this item?';
        }
        modal.classList.remove('hidden');
    }

    closeConfirmModal() {
        document.getElementById('confirm-modal').classList.add('hidden');
        this.pendingDeleteId = null;
        this.isDeletingAll = false;
    }

    async processDelete() {
        try {
            if (this.isDeletingAll) {
                await stockItemService.deleteAll();
                this.showToast('success', 'All items deleted successfully.');
            } else if (this.pendingDeleteId) {
                await stockItemService.delete(this.pendingDeleteId);
                this.showToast('success', 'Item deleted successfully.');
            }
            await this.loadStocks();
        } catch (error) {
            this.showToast('error', 'Delete failed: ' + error.message);
        } finally {
            this.closeConfirmModal();
        }
    }

    openModal(stock = null) {
        const modal = document.getElementById('stock-modal');
        const title = document.getElementById('modal-title');
        const form = document.getElementById('stock-form');
        if (stock) {
            title.textContent = 'Edit Item';
            document.getElementById('stock-id').value = stock.id;
            document.getElementById('stock-name').value = stock.name;
            document.getElementById('stock-category').value = stock.category;
            document.getElementById('stock-quantity').value = stock.quantity;
            document.getElementById('stock-unit').value = stock.unit;
        } else {
            title.textContent = 'Add Item';
            form.reset();
            document.getElementById('stock-id').value = '';
            document.getElementById('stock-category').value = 'INGREDIENT';
        }
        modal.classList.remove('hidden');
    }

    closeModals() {
        document.getElementById('stock-modal').classList.add('hidden');
        this.closeConfirmModal();
    }

    async handleFormSubmit(event) {
        event.preventDefault();
        const id = document.getElementById('stock-id').value;
        const stockData = {
            name: document.getElementById('stock-name').value,
            category: document.getElementById('stock-category').value,
            quantity: parseFloat(document.getElementById('stock-quantity').value),
            unit: document.getElementById('stock-unit').value
        };
        try {
            if (id) {
                await stockItemService.update(id, stockData);
                this.showToast('success', 'Item updated successfully.');
            } else {
                await stockItemService.create(stockData);
                this.showToast('success', 'Item added successfully.');
            }
            this.closeModals();
            await this.loadStocks();
        } catch (error) {
            this.showToast('error', 'Operation failed: ' + error.message);
        }
    }
}

export function init() {
    new StockPage();
}

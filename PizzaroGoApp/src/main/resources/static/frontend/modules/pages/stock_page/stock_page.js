import { stockService } from '../../../domains/stock/StockService.js';

export class StockPage {
    constructor() {
        this.init();
    }

    async init() {
        this.bindEvents();
        await this.loadStocks();
    }

    bindEvents() {
        // Import Button
        document.getElementById('btn-import').addEventListener('click', () => {
            document.getElementById('file-input').click();
        });

        document.getElementById('file-input').addEventListener('change', (e) => this.handleImport(e));

        // Export Button
        document.getElementById('btn-export').addEventListener('click', () => this.handleExport());

        // Add Stock Button
        document.getElementById('btn-add-stock').addEventListener('click', () => this.openModal());

        // Modal Close
        document.querySelector('.close-modal').addEventListener('click', () => this.closeModal());
        document.querySelector('.close-modal-btn').addEventListener('click', () => this.closeModal());

        // Form Submit
        document.getElementById('stock-form').addEventListener('submit', (e) => this.handleFormSubmit(e));
    }

    async loadStocks() {
        try {
            const stocks = await stockService.getAll();
            this.renderTable(stocks);
        } catch (error) {
            console.error('Error loading stocks:', error);
            alert('Failed to load stocks.');
        }
    }

    renderTable(stocks) {
        const tbody = document.getElementById('stock-table-body');
        tbody.innerHTML = '';

        stocks.forEach(stock => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${stock.id}</td>
                <td>${stock.name}</td>
                <td>${stock.quantity}</td>
                <td>${stock.unit}</td>
                <td>
                    <button class="action-btn edit-btn" data-id="${stock.id}"><i class="fas fa-pencil-alt"></i></button>
                    <button class="action-btn delete-btn" data-id="${stock.id}"><i class="fas fa-trash"></i></button>
                </td>
            `;

            // Bind actions for this row
            tr.querySelector('.edit-btn').addEventListener('click', () => this.openModal(stock));
            tr.querySelector('.delete-btn').addEventListener('click', () => this.handleDelete(stock.id));

            tbody.appendChild(tr);
        });
    }

    async handleImport(event) {
        const file = event.target.files[0];
        if (!file) return;

        try {
            const response = await stockService.importStocks(file);
            this.showToast('success', response.message || 'Import successful!');
            await this.loadStocks();
        } catch (error) {
            this.showToast('error', 'Import failed: ' + error.message);
        } finally {
            event.target.value = ''; // Reset input
        }
    }

    showToast(type, message) {
        // Create toast container if it doesn't exist (or use a library, but simple JS here)
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

        // Remove after 3 seconds
        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

    async handleExport() {
        try {
            await stockService.exportStocks();
        } catch (error) {
            alert('Export failed: ' + error.message);
        }
    }

    async handleDelete(id) {
        if (confirm('Are you sure you want to delete this item?')) {
            try {
                await stockService.delete(id);
                await this.loadStocks();
            } catch (error) {
                alert('Delete failed: ' + error.message);
            }
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
            document.getElementById('stock-quantity').value = stock.quantity;
            document.getElementById('stock-unit').value = stock.unit;
        } else {
            title.textContent = 'Add Item';
            form.reset();
            document.getElementById('stock-id').value = '';
        }

        modal.classList.remove('hidden');
    }

    closeModal() {
        document.getElementById('stock-modal').classList.add('hidden');
    }

    async handleFormSubmit(event) {
        event.preventDefault();

        const id = document.getElementById('stock-id').value;
        const stockData = {
            name: document.getElementById('stock-name').value,
            quantity: parseFloat(document.getElementById('stock-quantity').value),
            unit: document.getElementById('stock-unit').value
        };

        try {
            if (id) {
                await stockService.update(id, stockData);
            } else {
                await stockService.add(stockData);
            }
            this.closeModal();
            await this.loadStocks();
        } catch (error) {
            alert('Operation failed: ' + error.message);
        }
    }
}

// Export a function to initialize the page
export function init() {
    new StockPage();
}

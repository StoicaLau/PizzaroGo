import { orderService } from '../../../domains/order/OrderService.js';

let currentEditingOrder = null;

export async function init() {
    console.log("Initializing Orders Page");
    setupEventListeners();
    await loadOrders();
}

function setupEventListeners() {
    const closeBtn = document.getElementById('close-edit-modal');
    if (closeBtn) closeBtn.onclick = () => document.getElementById('edit-order-modal').classList.add('hidden');

    const saveBtn = document.getElementById('save-order-btn');
    if (saveBtn) saveBtn.onclick = saveOrderChanges;
}

async function loadOrders() {
    const list = document.getElementById('orders-list');
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user) {
        list.innerHTML = `
            <div class="text-center">
                <p class="no-orders-msg">Please login to view your orders.</p>
                <button class="btn-primary" style="margin-top:20px;" onclick="window.dispatchEvent(new CustomEvent('open-auth-modal'))">Login</button>
            </div>
        `;
        return;
    }

    try {
        const orders = await orderService.getByUserId(user.id);

        // reverse to show newest first
        if (orders.length > 0 && orders[0].id) {
            orders.sort((a, b) => b.id - a.id);
        }

        renderOrders(orders);
    } catch (error) {
        console.error("Error loading orders:", error);
        list.innerHTML = `<div class="error-msg text-center">Failed to load orders: ${error.message}</div>`;
    }
}

function renderOrders(orders) {
    const list = document.getElementById('orders-list');
    list.innerHTML = '';

    if (orders.length === 0) {
        list.innerHTML = '<p class="no-orders-msg">You haven\'t placed any orders yet. <br> <a href="#" onclick="navigate(\'/menu\'); return false;" style="color:var(--primary-color); font-size:1rem;">Go to Menu</a></p>';
        return;
    }

    orders.forEach(order => {
        const card = document.createElement('div');
        card.className = 'order-card';

        let statusClass = 'status-pending';
        if (order.status === 'COMPLETED') statusClass = 'status-completed';
        if (order.status === 'CANCELLED') statusClass = 'status-cancelled';

        const date = order.createdAt ? new Date(order.createdAt).toLocaleString() : 'Just now';

        let itemsHtml = '';
        if (order.orderItems && order.orderItems.length > 0) {
            itemsHtml = `<div class="order-items">
                ${order.orderItems.map(item => `
                    <div class="order-item-row">
                        <div>
                            <span class="item-qty">${item.quantity}x</span>
                            <span class="item-name">${item.menuProductName || 'Product'}</span>
                        </div>
                        <span class="item-price">${(item.totalPrice || 0).toFixed(2)} RON</span>
                    </div>
                `).join('')}
            </div>`;
        }

        card.innerHTML = `
            <div class="order-header">
                <div class="order-id">Order <span>#${order.id}</span></div>
                <div class="order-status ${statusClass}">${order.status}</div>
            </div>
            <div class="order-meta">
                <span class="order-date"><i class="far fa-calendar-alt"></i> ${date}</span>
            </div>
            ${itemsHtml}
            <div class="order-footer">
                <div class="price-breakdown">
                    <div class="price-row">
                        <span>Subtotal:</span>
                        <span>${(order.orderPrice || 0).toFixed(2)} RON</span>
                    </div>
                    <div class="price-row">
                        <span>Delivery:</span>
                        <span>${(order.deliveryPrice || 0).toFixed(2)} RON</span>
                    </div>
                    <div class="price-row total">
                        <span>Total:</span>
                        <span>${(order.totalPrice || 0).toFixed(2)} RON</span>
                    </div>
                </div>
                <div class="order-actions">
                    ${order.status === 'PENDING' ? `
                        <button class="btn-edit" onclick="window.openEditOrder(${order.id})"><i class="fas fa-edit"></i> Edit</button>
                        <button class="btn-cancel" onclick="window.cancelOrder(${order.id})"><i class="fas fa-times"></i> Cancel</button>
                    ` : ''}
                </div>
            </div>
        `;
        list.appendChild(card);
    });
}

window.openEditOrder = async (orderId) => {
    try {
        const order = await orderService.getById(orderId);
        currentEditingOrder = JSON.parse(JSON.stringify(order)); // Deep clone

        document.getElementById('edit-order-id-display').textContent = orderId;
        renderEditItems();
        updateEditTotal();

        document.getElementById('edit-order-modal').classList.remove('hidden');
    } catch (error) {
        alert("Failed to load order details: " + error.message);
    }
};

function renderEditItems() {
    const list = document.getElementById('edit-order-items-list');
    list.innerHTML = '';

    currentEditingOrder.orderItems.forEach((item, index) => {
        const itemRow = document.createElement('div');
        itemRow.className = 'edit-item-row';
        itemRow.innerHTML = `
            <div class="item-info">
                <div class="item-name">${item.menuProductName}</div>
                <div class="item-price-unit">${(item.totalPrice / item.quantity).toFixed(2)} RON / unit</div>
            </div>
            <div class="edit-item-qty-controls">
                <button class="btn-sm" onclick="window.updateEditQty(${index}, ${item.quantity - 1})">-</button>
                <span>${item.quantity}</span>
                <button class="btn-sm" onclick="window.updateEditQty(${index}, ${item.quantity + 1})">+</button>
                <button class="btn-sm text-danger" onclick="window.removeEditItem(${index})"><i class="fas fa-trash"></i></button>
            </div>
        `;
        list.appendChild(itemRow);
    });
}

window.updateEditQty = (index, newQty) => {
    if (newQty < 1) return;
    const item = currentEditingOrder.orderItems[index];
    const unitPrice = item.totalPrice / item.quantity;
    item.quantity = newQty;
    item.totalPrice = unitPrice * newQty;
    renderEditItems();
    updateEditTotal();
};

window.removeEditItem = (index) => {
    currentEditingOrder.orderItems.splice(index, 1);
    renderEditItems();
    updateEditTotal();
};

function updateEditTotal() {
    const subtotal = currentEditingOrder.orderItems.reduce((sum, item) => sum + item.totalPrice, 0);
    const total = subtotal + (currentEditingOrder.deliveryPrice || 0);
    document.getElementById('edit-order-total-display').textContent = `${total.toFixed(2)} RON`;
}

async function saveOrderChanges() {
    if (currentEditingOrder.orderItems.length === 0) {
        alert("Order must have at least one item!");
        return;
    }

    const saveBtn = document.getElementById('save-order-btn');
    saveBtn.disabled = true;
    saveBtn.textContent = 'Saving...';

    try {
        const updateData = {
            id: currentEditingOrder.id,
            userId: currentEditingOrder.userId,
            status: currentEditingOrder.status,
            deliveryPrice: currentEditingOrder.deliveryPrice,
            orderItems: currentEditingOrder.orderItems.map(item => ({
                id: item.id,
                orderId: currentEditingOrder.id,
                menuProductId: item.menuProductId,
                quantity: item.quantity,
                totalPrice: item.totalPrice,
                status: item.status
            }))
        };

        await orderService.update(updateData);
        document.getElementById('edit-order-modal').classList.add('hidden');
        await loadOrders(); // Reload the list
        alert("Order updated successfully!");
    } catch (error) {
        alert("Failed to update order: " + error.message);
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = 'Save Changes';
    }
}

window.cancelOrder = async (orderId) => {
    if (!confirm(`Are you sure you want to cancel order #${orderId}? This cannot be undone.`)) return;

    try {
        await orderService.deleteById(orderId);
        await loadOrders(); // Refresh list
        alert("Order cancelled successfully.");
    } catch (error) {
        alert("Failed to cancel order: " + error.message);
    }
};

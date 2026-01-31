import { orderService } from '../../../domains/order/OrderService.js';

export async function init() {
    console.log("Initializing Orders Page (Compact View)");
    await loadOrders();
}

async function loadOrders() {
    const list = document.getElementById('orders-list');
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user) {
        list.innerHTML = `<div class="text-center"><p class="no-orders-msg">Please login to manage orders.</p></div>`;
        return;
    }

    try {
        // Fetch all active orders for the manager/user
        // Requirement implies manager usage: if user is admin, we might want ALL,
        // but for now stick to current user (or list all if preferred)
        let orders = await orderService.getByUserId(user.id);

        // Sorting Logic:
        // Prio 1: NOT READY && NOT DELIVERED/CANCELLED
        // Prio 2: READY
        // Prio 3: DELIVERED/CANCELLED
        const getPriority = (status) => {
            const s = status ? status.toUpperCase() : 'PENDING';
            if (s === 'PENDING') return 1;
            if (s === 'PROCESSING') return 2;
            if (s === 'READY') return 3;
            if (s === 'DELIVERED') return 4;
            return 5; // Cancelled/Other
        };

        orders.sort((a, b) => {
            const pA = getPriority(a.status);
            const pB = getPriority(b.status);
            if (pA !== pB) return pA - pB;
            return b.id - a.id; // Newest first within same priority
        });

        const totalCount = document.getElementById('total-orders-count');
        if (totalCount) totalCount.textContent = orders.length;

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
        list.innerHTML = '<p class="no-orders-msg">No orders found.</p>';
        return;
    }

    orders.forEach(order => {
        const div = document.createElement('div');
        div.className = 'order-card';
        div.id = `order-card-${order.id}`;

        const status = (order.status || 'PENDING').toUpperCase();
        const date = order.createdAt ? new Date(order.createdAt).toLocaleString() : 'Just now';

        let itemsHtml = '';
        const maxInitialItems = 2; // Show only 2 items initially for uniform height
        const hasMoreItems = order.orderItems && order.orderItems.length > maxInitialItems;

        if (order.orderItems) {
            itemsHtml = order.orderItems.map((item, index) => {
                const itemStatus = (item.status || 'PENDING').toUpperCase();
                const isHidden = index >= maxInitialItems ? 'style="display:none;" data-expandable="true"' : '';
                return `
                    <div class="order-item-row" ${isHidden}>
                        <div class="item-main">
                            <div style="flex: 1;">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                                    <div>
                                        <span class="item-qty">${item.quantity}x</span>
                                        <span class="item-name">${item.menuProductName}</span>
                                    </div>
                                    <span class="status-badge status-${itemStatus.toLowerCase()}" style="font-size: 0.65rem; padding: 4px 10px;">${itemStatus}</span>
                                </div>
                                <div class="item-desc">${item.menuProductDescription || ''}</div>
                            </div>
                            <span class="item-price" style="margin-left: 15px;">${(item.totalPrice || 0).toFixed(2)} RON</span>
                        </div>
                    </div>
                `;
            }).join('');
        }

        div.innerHTML = `
            <div class="order-header">
                <div class="order-id">Order #${order.id} <span>${date}</span></div>
                <div class="status-badge status-${status.toLowerCase()}">${status}</div>
            </div>
            
            <div class="order-items-container">
                <div class="order-items">
                    ${itemsHtml}
                </div>
            </div>

            ${hasMoreItems ? `
                <button class="btn-extend" id="extend-btn-${order.id}" onclick="window.toggleExtended(${order.id})">
                    <i class="fas fa-chevron-down"></i> View ${order.orderItems.length - maxInitialItems} more items
                </button>
            ` : ''}

            <div class="order-footer">
                <div class="total-row">
                    <div class="total-label">
                         <span>Subtotal: ${(order.orderPrice || 0).toFixed(2)}</span>
                         <span>Delivery: ${(order.deliveryPrice || 0).toFixed(2)}</span>
                    </div>
                    <span class="total-value">${(order.totalPrice || 0).toFixed(2)} RON</span>
                </div>
                <div class="order-main-actions">
                    ${status === 'READY' ?
                `<button class="btn-action btn-deliver" onclick="window.updateOrderStatusAction(${order.id}, 'DELIVERED')">
                            <i class="fas fa-check-double"></i> DELIVER ORDER
                        </button>`
                : ''}
                    ${status === 'PENDING' ?
                `<button class="btn-action btn-cancel-item" onclick="window.cancelOrder(${order.id})">
                            <i class="fas fa-times"></i> CANCEL ORDER
                        </button>`
                : ''}
                </div>
            </div>
        `;
        list.appendChild(div);
    });
}

window.toggleExtended = (orderId) => {
    const card = document.getElementById(`order-card-${orderId}`);
    const btn = document.getElementById(`extend-btn-${orderId}`);
    const isExtended = card.classList.toggle('extended');

    // Toggle visibility of extra items
    const extraItems = card.querySelectorAll('[data-expandable="true"]');
    extraItems.forEach(item => {
        item.style.display = isExtended ? 'block' : 'none';
    });

    if (btn) {
        btn.innerHTML = isExtended ?
            `<i class="fas fa-chevron-up"></i> Show less` :
            `<i class="fas fa-chevron-down"></i> View ${extraItems.length} more items`;
    }
};

window.updateOrderStatusAction = async (orderId, status) => {
    try {
        await orderService.updateStatus({ id: orderId, status: status });
        await loadOrders();
    } catch (e) {
        alert("Action failed: " + e.message);
    }
};

window.cancelOrder = async (orderId) => {
    if (!confirm(`Cancel order #${orderId}?`)) return;
    try {
        await orderService.deleteById(orderId);
        await loadOrders();
    } catch (error) {
        alert("Error: " + error.message);
    }
};

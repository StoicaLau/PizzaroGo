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
        let orders = await orderService.getByUserId(user.id);

        const getPriority = (status) => {
            const s = status ? status.toUpperCase() : 'PENDING';
            if (s === 'PENDING' || s === 'PROCESSING' || s === 'READY') return 1;
            return 2; // DELIVERED, CANCELED
        };

        console.log("Orders received from API:", orders);

        orders.sort((a, b) => {
            const pA = getPriority(a.status);
            const pB = getPriority(b.status);
            if (pA !== pB) return pA - pB;
            return b.id - a.id; // Newest first
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
        list.innerHTML = `
            <div class="loading-spinner">
                <i class="fas fa-pizza-slice" style="font-size: 3rem; color: #333; margin-bottom: 20px;"></i>
                <p>You haven't placed any orders yet.</p>
                <button class="btn-primary-sm" style="max-width: 200px; margin: 20px auto;" onclick="navigate('/menu')">Go to Menu</button>
            </div>
        `;
        return;
    }

    orders.forEach(order => {
        const div = document.createElement('div');
        div.className = 'order-card';
        div.id = `order-card-${order.id}`;

        const status = (order.status || 'PENDING').toUpperCase();
        const date = order.createdAt ? new Date(order.createdAt).toLocaleDateString() : 'Today';
        const time = order.createdAt ? new Date(order.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';

        let itemsHtml = '';
        const maxInitialItems = 2;
        const hasMoreItems = order.orderItems && order.orderItems.length > maxInitialItems;

        if (order.orderItems) {
            itemsHtml = order.orderItems.map((item, index) => {
                const isHidden = index >= maxInitialItems ? 'style="display:none;" data-expandable="true"' : '';
                return `
                    <div class="order-item-chip" ${isHidden}>
                        <div class="item-head">
                            <span class="item-title">${item.menuProductName}</span>
                            <span class="item-qty">x${item.quantity}</span>
                        </div>
                        ${item.menuProductDescription ? `<div class="item-description">${item.menuProductDescription}</div>` : ''}
                    </div>
                `;
            }).join('');
        }

        div.innerHTML = `
            <div class="order-header">
                <div class="order-id-group">
                    <span class="order-id">#${order.id}</span>
                    <span class="order-date">${date} • ${time}</span>
                </div>
                <div class="status-badge status-${status.toLowerCase()}">${status}</div>
            </div>
            
            <div class="order-items-wrapper">
                <div class="order-item-list">
                    ${itemsHtml}
                </div>
            </div>

            ${hasMoreItems ? `
                <button class="btn-extend" id="extend-btn-${order.id}" onclick="window.toggleExtended(${order.id})">
                    <i class="fas fa-chevron-down"></i> View ${order.orderItems.length - maxInitialItems} more items
                </button>
            ` : ''}

            <div class="order-footer">
                <div class="price-summary">
                    <span class="total-label">Subtotal inkl. delivery</span>
                    <span class="total-amount">${(order.totalPrice || 0).toFixed(2)} RON</span>
                </div>
                <div class="card-actions">
                    ${status === 'READY' ? `
                        <button class="btn-primary-sm" onclick="window.updateOrderStatusAction(${order.id}, 'DELIVERED')" style="flex: 1; justify-content: center; background: #2ecc71;">
                            <i class="fas fa-check-circle"></i> Mark as Delivered
                        </button>
                    ` : status === 'PENDING' ? `
                        <button class="btn-danger-sm" onclick="window.cancelOrder(${order.id})" style="flex: 1; justify-content: center;">
                            <i class="fas fa-times-circle"></i> Cancel Order
                        </button>
                    ` : (status === 'DELIVERED' || status === 'CANCELED') ? `
                        <button class="btn-primary-sm" onclick="window.reorder(${order.id})" style="flex: 1;">
                            <i class="fas fa-redo"></i> Reorder Now
                        </button>
                    ` : `
                        <div class="status-info" style="font-size: 0.85rem; color: #aaa; text-align: center; width: 100%;">
                            <i class="fas fa-spinner fa-spin"></i> Preparation in progress...
                        </div>
                    `}
                </div>
            </div>
        `;
        list.appendChild(div);
    });
}

function showPremiumConfirm(title, body, onApprove) {
    const modal = document.getElementById('premium-confirm-modal');
    document.getElementById('confirm-modal-title').textContent = title;
    document.getElementById('confirm-modal-body').textContent = body;

    const approveBtn = document.getElementById('confirm-modal-approve');
    const cancelBtn = document.getElementById('confirm-modal-cancel');

    approveBtn.onclick = () => {
        modal.classList.add('hidden');
        onApprove();
    };

    cancelBtn.onclick = () => {
        modal.classList.add('hidden');
    };

    modal.classList.remove('hidden');
}

function showPremiumFeedback(title, body) {
    const modal = document.getElementById('premium-feedback-modal');
    document.getElementById('feedback-modal-title').textContent = title;
    document.getElementById('feedback-modal-body').textContent = body;
    modal.classList.remove('hidden');
}

window.reorder = async (orderId) => {
    showPremiumConfirm(
        "Reorder Items?",
        "Do you want to add these items to a new order and place it now?",
        async () => {
            try {
                const user = JSON.parse(localStorage.getItem('user'));
                const originalOrder = await orderService.getById(orderId);

                if (!originalOrder || !originalOrder.orderItems) {
                    throw new Error("Could not find original order details.");
                }

                const newOrderData = {
                    userId: user.id,
                    status: 'PENDING',
                    deliveryPrice: originalOrder.deliveryPrice || 0,
                    orderPrice: originalOrder.orderPrice,
                    totalPrice: originalOrder.totalPrice,
                    orderItems: originalOrder.orderItems.map(item => ({
                        menuProductId: item.menuProductId,
                        quantity: item.quantity,
                        totalPrice: item.totalPrice,
                        status: 'PENDING'
                    }))
                };

                await orderService.create(newOrderData);
                showPremiumFeedback("Awesome!", "🍕 Your reorder has been placed and is now in the oven.");
                await loadOrders();
            } catch (e) {
                console.error("Reorder failed:", e);
                alert("Failed to reorder: " + e.message);
            }
        }
    );
};

window.toggleExtended = (orderId) => {
    const card = document.getElementById(`order-card-${orderId}`);
    const btn = document.getElementById(`extend-btn-${orderId}`);
    const isExtended = card.classList.toggle('extended');

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
    showPremiumConfirm(
        "Cancel Order?",
        `Are you sure you want to cancel order #${orderId}? It will be marked as CANCELED.`,
        async () => {
            try {
                await orderService.updateStatus({ id: orderId, status: 'CANCELED' });
                showPremiumFeedback("Cancelled", "🗑️ Order cancelled successfully.");
                await loadOrders();
            } catch (error) {
                alert("Error: " + error.message);
            }
        }
    );
};

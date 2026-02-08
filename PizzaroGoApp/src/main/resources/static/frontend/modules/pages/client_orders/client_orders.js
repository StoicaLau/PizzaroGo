import { orderService } from '../../../domains/order/OrderService.js';
import { orderItemService } from '../../../domains/oredrItem/OrderItemService.js';

export function init() {
    console.log("Client Orders: Init started");

    // Check if DOM is ready
    const pendingList = document.getElementById('pending-orders-grid');
    if (!pendingList) {
        console.error("Client Orders: DOM elements not found (pending-orders-grid). Retrying in 100ms...");
        setTimeout(init, 100);
        return;
    }

    console.log("Client Orders: DOM Start");
    loadOrders();

    // Event Listeners for Modals
    const cancelBtn = document.getElementById('cancel-start-btn');
    if (cancelBtn) cancelBtn.addEventListener('click', closeModal);

    const confirmBtn = document.getElementById('confirm-start-btn');
    if (confirmBtn) confirmBtn.addEventListener('click', confirmStartOrder);

    const closeDetailsBtn = document.getElementById('close-details-btn');
    if (closeDetailsBtn) closeDetailsBtn.addEventListener('click', closeDetailsModal);

    const deliverBtn = document.getElementById('deliver-order-btn');
    if (deliverBtn) deliverBtn.addEventListener('click', markOrderReady);
}

// Global Toggle Function
window.toggleColumn = function (btn) {
    const column = btn.closest('.orders-column');
    if (!column) return;

    column.classList.toggle('collapsed');

    // Rotate/Flip icon logic
    // If collapsed, we might want to change icon or rotate it
    const icon = btn.querySelector('i');
    if (column.classList.contains('collapsed')) {
        btn.innerHTML = '<i class="fas fa-chevron-right"></i>'; // Change to sideways or just rotate
    } else {
        btn.innerHTML = '<i class="fas fa-chevron-up"></i>';
    }
}

let allOrders = [];
let currentOrderId = null; // For start modal
let activeProcessingOrderId = null; // For details modal

async function loadOrders() {
    console.log("Client Orders: Fetching active orders...");
    try {
        allOrders = await orderService.getActiveOrders();
        console.log(`Client Orders: Received ${allOrders.length} orders`);
        renderOrders();
    } catch (error) {
        console.error('Client Orders: Error loading orders:', error);
        // Show detailed error
        const msg = error.message || JSON.stringify(error);
        alert('Error loading orders: ' + msg);
    }
}

function renderOrders() {
    // Clear stats
    let pendingCount = 0;
    let processingCount = 0;

    const pendingContainer = document.getElementById('pending-orders-grid');
    const processingContainer = document.getElementById('processing-orders-grid');

    if (pendingContainer) pendingContainer.innerHTML = '';
    if (processingContainer) processingContainer.innerHTML = '';

    allOrders.forEach(order => {
        try {
            const statusRaw = order.status ? String(order.status) : '';
            const status = statusRaw.trim().toUpperCase();

            if (status === 'PENDING') {
                pendingCount++;
                if (pendingContainer) pendingContainer.appendChild(createKDSCard(order, 'pending'));
            } else if (status === 'PROCESSING') {
                processingCount++;
                if (processingContainer) processingContainer.appendChild(createKDSCard(order, 'processing'));
            }
        } catch (e) {
            console.error(`Error rendering order #${order.id}:`, e);
        }
    });

    // Update Header Stats
    const statPending = document.getElementById('stat-pending');
    const statProcessing = document.getElementById('stat-processing');
    if (statPending) statPending.innerText = pendingCount;
    if (statProcessing) statProcessing.innerText = processingCount;

    if (activeProcessingOrderId) {
        const order = allOrders.find(o => o.id === activeProcessingOrderId);
        if (order) renderOrderDetailsInModal(order);
    }
}

function createKDSCard(order, type) {
    const div = document.createElement('div');
    div.className = 'order-card';

    // Calculate total items
    const totalItems = order.orderItems ? order.orderItems.length : 0;

    // Time Logic
    let timeHtml = '';

    if (type === 'pending') {
        const placed = new Date(order.createdAt);
        timeHtml = `
            <div class="time-row">
                <span class="lbl">Placed:</span>
                <span class="val">${placed.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
            </div>
        `;
    } else {
        const placed = new Date(order.createdAt);
        const due = new Date(order.estimatedAt);
        timeHtml = `
            <div class="time-row">
                <span class="lbl">Placed:</span>
                <span class="val">${placed.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
            </div>
            <div class="time-row">
                <span class="lbl">Due:</span>
                <span class="val accent">${due.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
            </div>
        `;
    }

    // Items Preview
    let itemsPreview = '';
    if (order.orderItems && order.orderItems.length > 0) {
        order.orderItems.slice(0, 3).forEach(item => {
            // Debugging: Check if description is present
            // console.log(`Order ${order.id} Item:`, item.menuProductName, "Desc:", item.menuProductDescription);

            itemsPreview += `<div class="item-preview-row">
                <div class="preview-main">${item.quantity}x ${item.menuProductName}</div>
                <div class="preview-sub">${item.menuProductDescription || ''}</div>
            </div>`;
        });
        if (order.orderItems.length > 3) {
            itemsPreview += `<div class="more-items">+${order.orderItems.length - 3} more...</div>`;
        }
    }

    div.innerHTML = `
        <div class="card-header">
            <span class="card-id">Order #${order.id}</span>
            <div class="card-time-stack">
                ${timeHtml}
            </div>
        </div>
        
        <div class="card-content">
            <div class="item-count">${totalItems} Items</div>
            <div class="items-list-preview" style="font-size: 0.9rem; color: #ccc; margin-top: 5px;">
                ${itemsPreview}
            </div>
        </div>

        <div class="card-actions">
            ${type === 'pending' ?
            `<button class="card-btn btn-cancel-text" onclick="event.stopPropagation(); window.cancelOrder(${order.id})">
                 Cancel
               </button>
               <button class="card-btn btn-start" onclick="event.stopPropagation(); window.openStartModal(${order.id})">
                 <i class="fas fa-fire"></i> START
               </button>` :
            `<button class="card-btn btn-cancel-text" onclick="event.stopPropagation(); window.cancelOrder(${order.id})">
                 Cancel
               </button>
               <button class="card-btn btn-view" onclick="event.stopPropagation(); window.openOrderDetailsById(${order.id})">
                 <i class="fas fa-eye"></i> VIEW
               </button>`
        }
        </div>
    `;

    // Make whole card clickable for processing
    if (type === 'processing') {
        div.onclick = () => window.openOrderDetailsById(order.id);
    }

    return div;
}

// Global helper since we pass ID in HTML
window.openOrderDetailsById = function (id) {
    const order = allOrders.find(o => o.id === id);
    if (order) openOrderDetails(order);
}

// --- Start Order Logic ---

// Quick Time Preset Logic
window.setQuickTime = function (minutes) {
    const btns = document.querySelectorAll('.time-btn');
    btns.forEach(b => b.classList.remove('selected'));

    // Find button that was clicked to add selected class visually
    // (This simple logic assumes usage via onclick)
    const eventTarget = event.currentTarget;
    if (eventTarget) eventTarget.classList.add('selected');

    const now = new Date();
    now.setMinutes(now.getMinutes() + minutes);

    // Format for datetime-local
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const min = String(now.getMinutes()).padStart(2, '0');

    const timeInput = document.getElementById('estimated-time');
    if (timeInput) timeInput.value = `${year}-${month}-${day}T${hours}:${min}`;
}

window.openStartModal = function (orderId) {
    currentOrderId = orderId;
    const modalId = document.getElementById('modal-order-id');
    const modal = document.getElementById('start-order-modal');

    // Use "Order #123" format
    if (modalId) modalId.innerText = orderId;
    // Wait, the HTML has "Start Order #<span...". So setting innerText to ID results in "Start Order #123".
    // If the user wants "Order #123", I should change the HTML structure or JS.
    // Let's assume the user is happy with "Order #123" appearing ANYWHERE.
    // The previous fix for the card was explicit.

    if (modal) modal.classList.remove('hidden');

    // Default to 30 mins
    window.setQuickTime(30);

    // New: Inject items summary
    const summaryBox = document.getElementById('start-order-items-summary');
    if (summaryBox) {
        summaryBox.innerHTML = '';
        const order = allOrders.find(o => o.id === orderId);
        if (order && order.orderItems) {
            order.orderItems.forEach(item => {
                const row = document.createElement('div');
                row.className = 'summary-item';
                // Try to find description if mapped, otherwise just Name
                const desc = item.description || item.menuProductDescription || '';

                row.innerHTML = `
                    <div style="flex:1">
                        <div style="font-weight:600;">${item.quantity} x ${item.menuProductName}</div>
                        ${desc ? `<div style="font-size:0.85rem; color:#aaa; margin-top:2px;">${desc}</div>` : ''}
                    </div>
                    <span style="color: #ffae00; font-weight:600;">${(item.totalPrice || 0).toFixed(2)} RON</span>
                `;
                summaryBox.appendChild(row);
            });
        }
    }
}

function closeModal() {
    const modal = document.getElementById('start-order-modal');
    console.log("closeModal called, modal element:", modal);
    if (modal) {
        modal.classList.add('hidden');
    } else {
        console.warn("start-order-modal not found in closeModal!");
    }
    currentOrderId = null;
}

async function confirmStartOrder() {
    if (!currentOrderId) {
        console.warn("confirmStartOrder called but currentOrderId is null");
        return;
    }

    const timeInput = document.getElementById('estimated-time');
    if (!timeInput) {
        alert("System error: estimated-time input not found.");
        return;
    }

    const selectedTime = timeInput.value;
    if (!selectedTime) {
        alert("Please select an estimated delivery time.");
        return;
    }

    console.log("Confirming start for order:", currentOrderId, "at time:", selectedTime);

    // Convert local time string to ISO for backend (LocalDateTime)
    const dateObj = new Date(selectedTime);

    const payload = {
        id: currentOrderId,
        status: "PROCESSING",
        estimatedAt: dateObj.toISOString()
    };

    try {
        console.log("Sending updateStatus payload:", payload);
        await orderService.updateStatus(payload);
        console.log("Update success, closing modal...");
        closeModal();
        console.log("Loading orders...");
        loadOrders();
    } catch (e) {
        console.error("Error in confirmStartOrder:", e);
        alert('Failed to start order: ' + (e.message || 'Unknown error'));
        // If it's a DOM error like classList, it might be caught here
    }
}

// --- Details / Processing Logic ---

function openOrderDetails(order) {
    activeProcessingOrderId = order.id;
    const detailsId = document.getElementById('details-order-id');
    const modal = document.getElementById('order-details-modal');

    if (detailsId) detailsId.innerText = order.id;
    if (modal) {
        modal.classList.remove('hidden');
    } else {
        console.warn("order-details-modal not found");
    }
    renderOrderDetailsInModal(order);
}

function renderOrderDetailsInModal(order) {
    const list = document.getElementById('order-items-list');
    list.innerHTML = '';

    let allReady = true;

    order.orderItems.forEach(item => {
        const isReady = item.status && item.status.toUpperCase() === 'READY';
        if (!isReady) allReady = false;

        const row = document.createElement('div');
        row.className = 'order-item-row';
        row.innerHTML = `
            <div class="item-name">
                <span class="main-text">${item.quantity}x ${item.menuProductName}</span>
                ${item.menuProductDescription ? `<div class="sub-text">${item.menuProductDescription}</div>` : ''}
                <div style="font-size:0.8rem; color:#aaa; margin-top:2px;">$${item.totalPrice ? item.totalPrice.toFixed(2) : '0.00'}</div>
            </div>
            <div class="item-status">
            <div class="item-status-actions">
                ${isReady
                ? `<button class="btn-ready-disabled" disabled>Ready</button>
                       <span class="undo-icon" onclick="window.setItemStatus(${item.id}, 'PROCESSING')" title="Mark as In Progress">⟲</span>`
                : `<button class="btn-mark-ready" onclick="window.setItemStatus(${item.id}, 'READY')">Mark Ready</button>`
            }
            </div>
        `;
        list.appendChild(row);
    });

    const deliverBtn = document.getElementById('deliver-order-btn');
    deliverBtn.disabled = !allReady;

    const statusText = document.getElementById('order-status-text');
    statusText.innerText = allReady ? "All items ready for delivery." : "Waiting for items...";
    statusText.style.color = allReady ? "#2ecc71" : "#aaaaaa";
}

window.setItemStatus = async function (itemId, newStatus) {
    try {
        await orderItemService.updateStatus(itemId, newStatus);
        const order = allOrders.find(o => o.id === activeProcessingOrderId);
        if (order) {
            // Refresh orders to get updated state
            await loadOrders();
            // Re-find and re-render details
            const updatedOrder = allOrders.find(o => o.id === activeProcessingOrderId);
            if (updatedOrder) renderOrderDetailsInModal(updatedOrder);
        } else {
            loadOrders();
        }
    } catch (e) {
        console.error(e);
        alert("Failed to update item status: " + e.message);
    }
}

function closeDetailsModal() {
    const modal = document.getElementById('order-details-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
    activeProcessingOrderId = null;
}

async function markOrderReady() {
    if (!activeProcessingOrderId) return;

    const payload = {
        id: activeProcessingOrderId,
        status: "READY"
    };

    try {
        await orderService.updateStatus(payload);
        closeDetailsModal();
        loadOrders();
    } catch (e) {
        console.error(e);
        alert("Failed to update status: " + e.message);
    }
}

window.markOrderReadyById = async function (id) {
    const payload = {
        id: id,
        status: "READY"
    };

    try {
        await orderService.updateStatus(payload);
        loadOrders();
    } catch (e) {
        console.error(e);
        alert("Failed to update status: " + e.message);
    }
}

window.cancelOrder = async function (orderId) {
    showPremiumConfirm(
        "Cancel Order?",
        `Are you sure you want to cancel order #${orderId}? It will be marked as CANCELED.`,
        async () => {
            try {
                await orderService.updateStatus({ id: orderId, status: 'CANCELED' });
                showPremiumFeedback("Cancelled", "🗑️ Order marked as cancelled successfully.");
                loadOrders();
                if (activeProcessingOrderId === orderId) closeDetailsModal();
            } catch (error) {
                console.error(error);
                alert("Error: " + error.message);
            }
        }
    );
};

function showPremiumConfirm(title, body, onApprove) {
    const modal = document.getElementById('premium-confirm-modal');
    if (!modal) return;

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
    if (!modal) return;

    document.getElementById('feedback-modal-title').textContent = title;
    document.getElementById('feedback-modal-body').textContent = body;
    modal.classList.remove('hidden');
}

// Expose functions globally for inline HTML
window.startOrder = function (id) { window.openStartModal(id); } // alias
window.loadClientOrders = loadOrders; // helper for debugging
window.closeModal = closeModal;
window.closeDetailsModal = closeDetailsModal;
window.confirmStartOrder = confirmStartOrder;

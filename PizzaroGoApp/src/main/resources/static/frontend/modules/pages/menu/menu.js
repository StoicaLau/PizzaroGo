import { menuProductService } from '../../../domains/menu_product/MenuProductService.js';
import { orderService } from '../../../domains/order/OrderService.js';
import { Cart } from '../../../shared/utils/Cart.js';

let allProducts = [];
let activeCategory = 'ALL';
let searchFilter = '';
let sortOption = 'name-asc';

export async function init() {
    console.log("Initializing Menu Page");
    setupEventListeners();
    await loadMenu();
    updateCartUI();

    // Hide cart if not authenticated
    if (!window.isAuthenticated()) {
        const cart = document.getElementById('cart-panel');
        if (cart) cart.style.display = 'none';
        const container = document.querySelector('.menu-container');
        if (container) container.classList.remove('cart-open');
    }

    // Check if we should open the cart (e.g. redirected from other page)
    if (localStorage.getItem('open_cart_on_load') === 'true') {
        localStorage.removeItem('open_cart_on_load');
        openCart();
    }
}

async function loadMenu() {
    const grid = document.getElementById('products-grid');
    try {
        allProducts = await menuProductService.getAll();
        renderCategories();
        renderProducts();
    } catch (error) {
        console.error("Error loading products:", error);
        grid.innerHTML = `<div class="error-msg">Failed to load menu: ${error.message}</div>`;
    }
}

function renderCategories() {
    const filterContainer = document.getElementById('category-filters');
    const categories = [...new Set(allProducts.map(p => p.productCategory))];

    // Clear dynamic buttons (keep 'All Items')
    const allBtn = filterContainer.querySelector('[data-category="ALL"]');
    filterContainer.innerHTML = '';
    filterContainer.appendChild(allBtn);

    categories.forEach(cat => {
        const btn = document.createElement('button');
        btn.className = 'filter-btn';
        btn.textContent = capitalize(cat);
        btn.dataset.category = cat;
        btn.onclick = () => filterByCategory(cat);
        filterContainer.appendChild(btn);
    });
}

function renderProducts() {
    const grid = document.getElementById('products-grid');
    if (!grid) return;
    grid.innerHTML = '';

    // Filter
    let filtered = allProducts;
    if (activeCategory !== 'ALL') {
        filtered = filtered.filter(p => p.productCategory === activeCategory);
    }
    if (searchFilter) {
        filtered = filtered.filter(p =>
            p.name.toLowerCase().includes(searchFilter.toLowerCase()) ||
            (p.description && p.description.toLowerCase().includes(searchFilter.toLowerCase()))
        );
    }

    // Sort
    filtered.sort((a, b) => {
        switch (sortOption) {
            case 'name-asc': return a.name.localeCompare(b.name);
            case 'name-desc': return b.name.localeCompare(a.name);
            case 'price-asc': return a.price - b.price;
            case 'price-desc': return b.price - a.price;
            default: return 0;
        }
    });

    if (filtered.length === 0) {
        grid.innerHTML = '<div class="no-results"><h3>No items found</h3><p>Try a different category or search term.</p></div>';
        return;
    }

    filtered.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
            <img src="${p.imageURL || 'https://via.placeholder.com/300x200?text=Delicious+Pizza'}" alt="${p.name}" class="product-image">
            <div class="product-info">
                <h2 class="product-name">${p.name}</h2>
                <div class="product-category">${p.productCategory}</div>
                ${p.description ? `<p class="product-description">${p.description}</p>` : ''}
                <div class="product-footer">
                    <span class="product-price">${p.price.toFixed(2)} RON</span>
                    <div class="add-to-cart-controls">
                        ${window.isAuthenticated() ? `
                            <input type="number" value="1" min="1" class="quantity-input" id="qty-${p.id}">
                            <button class="btn-add" data-id="${p.id}" title="Add to Cart"><i class="fas fa-cart-plus"></i></button>
                        ` : `
                            <button class="btn-add login-to-order" onclick="window.dispatchEvent(new CustomEvent('open-auth-modal'))">Login to Order</button>
                        `}
                    </div>
                </div>
            </div>
        `;

        const addBtn = card.querySelector(`.btn-add[data-id="${p.id}"]`);
        if (addBtn) {
            addBtn.onclick = () => {
                const qtyInput = document.getElementById(`qty-${p.id}`);
                const qty = parseInt(qtyInput.value) || 1;
                Cart.addItem(p, qty);
                openCart();
            };
        }

        grid.appendChild(card);
    });
}

function filterByCategory(category) {
    activeCategory = category;
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.category === category);
    });
    renderProducts();
}

function setupEventListeners() {
    // Search & Sort
    const searchInput = document.getElementById('menu-search');
    if (searchInput) {
        searchInput.oninput = (e) => {
            searchFilter = e.target.value;
            renderProducts();
        };
    }

    const sortSelect = document.getElementById('menu-sort');
    if (sortSelect) {
        sortSelect.onchange = (e) => {
            sortOption = e.target.value;
            renderProducts();
        };
    }

    const allBtn = document.querySelector('[data-category="ALL"]');
    if (allBtn) allBtn.onclick = () => filterByCategory('ALL');

    const closeBtn = document.getElementById('close-cart-btn');
    if (closeBtn) closeBtn.onclick = closeCart;

    const finishBtn = document.getElementById('finish-order-btn');
    if (finishBtn) finishBtn.onclick = handleFinishOrder;

    // Listen to cart changes
    window.removeEventListener('cart-change', updateCartUI);
    window.addEventListener('cart-change', updateCartUI);

    // Listen for global open cart requests
    window.addEventListener('open-menu-cart', openCart);
}

function openCart() {
    if (!window.isAuthenticated()) {
        window.dispatchEvent(new CustomEvent('open-auth-modal'));
        return;
    }
    const cart = document.getElementById('cart-panel');
    const container = document.querySelector('.menu-container');
    if (cart) {
        cart.style.display = 'flex';
        cart.classList.add('open');
    }
    if (container) container.classList.add('cart-open');
}

function closeCart() {
    const cart = document.getElementById('cart-panel');
    const container = document.querySelector('.menu-container');
    if (cart) cart.classList.remove('open');
    if (container) container.classList.remove('cart-open');
}

function updateCartUI() {
    const list = document.getElementById('cart-items-list');
    const totalEl = document.getElementById('cart-total-amount');
    const items = Cart.getItems();

    if (!list) return;

    list.innerHTML = '';
    items.forEach(item => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'cart-item';
        itemDiv.innerHTML = `
            <img src="${item.imageURL || 'https://via.placeholder.com/60'}" class="cart-item-img">
            <div class="cart-item-details">
                <div class="cart-item-name">${item.menuProductName}</div>
                <div class="cart-item-price">${item.price.toFixed(2)} RON x ${item.quantity}</div>
            </div>
            <div class="cart-item-actions">
                <button class="btn-sm" onclick="event.stopPropagation(); window.updateItemQty(${item.menuProductId}, ${item.quantity - 1})">-</button>
                <span>${item.quantity}</span>
                <button class="btn-sm" onclick="event.stopPropagation(); window.updateItemQty(${item.menuProductId}, ${item.quantity + 1})">+</button>
                <button class="btn-sm text-danger" onclick="event.stopPropagation(); window.removeCartItem(${item.menuProductId})"><i class="fas fa-trash"></i></button>
            </div>
        `;
        list.appendChild(itemDiv);
    });

    totalEl.textContent = `${Cart.getTotal().toFixed(2)} RON`;
}

// Global helpers for cart actions in dynamic HTML
window.updateItemQty = (id, qty) => {
    if (qty <= 0) {
        Cart.removeItem(id);
    } else {
        Cart.updateQuantity(id, qty);
    }
};

window.removeCartItem = (id) => Cart.removeItem(id);

async function handleFinishOrder() {
    const items = Cart.getItems();
    if (items.length === 0) {
        alert("Cart is empty!");
        return;
    }

    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) {
        window.dispatchEvent(new CustomEvent('open-auth-modal'));
        return;
    }

    const finishBtn = document.getElementById('finish-order-btn');
    finishBtn.disabled = true;
    finishBtn.textContent = 'Processing...';

    const cartTotal = Cart.getTotal();
    const deliveryPrice = 0.0;

    const orderData = {
        userId: user.id,
        status: 'PENDING',
        deliveryPrice: deliveryPrice,
        orderPrice: cartTotal,
        totalPrice: cartTotal + deliveryPrice,
        orderItems: items.map(item => ({
            menuProductId: item.menuProductId,
            quantity: item.quantity,
            totalPrice: item.totalPrice,
            status: 'PENDING'
        }))
    };



    try {
        await orderService.create(orderData);
        Cart.clear();
        closeCart();
        document.getElementById('order-success-modal').classList.remove('hidden');
    } catch (error) {
        console.error("Order failed:", error);
        alert("Failed to place order: " + error.message);
    } finally {
        finishBtn.disabled = false;
        finishBtn.textContent = 'Finish Order';
    }
}

function capitalize(str) {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

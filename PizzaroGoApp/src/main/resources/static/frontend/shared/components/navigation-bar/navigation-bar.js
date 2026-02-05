(async function () {
    // Helper to load CSS
    function loadCss(href) {
        if (!document.querySelector(`link[href="${href}"]`)) {
            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = href;
            document.head.appendChild(link);
        }
    }

    // 1. Load Header User Details Component
    try {
        const hudRes = await fetch('/frontend/shared/components/header-user-details/header-user-details.html');
        if (hudRes.ok) {
            const html = await hudRes.text();
            const placeholder = document.getElementById('header-user-details-placeholder');
            if (placeholder) {
                placeholder.innerHTML = html;
                loadCss('/frontend/shared/components/header-user-details/header-user-details.css');
                await import('/frontend/shared/components/header-user-details/header-user-details.js');
            }
        }
    } catch (e) {
        console.error("Failed to load header-user-details", e);
    }

    // 2. Load User Authentication Modal (Global)
    if (!document.getElementById('auth-modal')) {
        try {
            const authRes = await fetch('/frontend/modules/components/user-authentication/user-authentication.html');
            if (authRes.ok) {
                const html = await authRes.text();
                // Append specific element to body to ensure it overlays everything
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;
                const modal = tempDiv.firstElementChild;
                document.body.appendChild(modal);

                loadCss('/frontend/modules/components/user-authentication/user-authentication.css');
                await import('/frontend/modules/components/user-authentication/user-authentication.js');
            }
        } catch (e) {
            console.error("Failed to load user-authentication", e);
        }
    }

    // 3. Navigation Logic
    // Existing logic for order button or other nav items
    document.addEventListener("click", e => {
        if (e.target.classList.contains("order-btn")) {
            if (window.navigate) window.navigate("/menu");
        }
    });

    // 4. Update sync logic for navigation items
    function syncAuthButtons() {
        const role = window.getUserRole ? window.getUserRole() : null;

        const navItems = {
            home: document.getElementById('nav-home'),
            admin: document.getElementById('nav-admin'),
            menu: document.getElementById('nav-menu'),
            orders: document.getElementById('nav-orders'),
            stocks: document.getElementById('nav-stocks'),
            products: document.getElementById('nav-products'),
            users: document.getElementById('nav-users'),
            clientOrders: document.getElementById('nav-client-orders')
        };

        // Anyone can see: Home, Menu
        if (navItems.home) navItems.home.style.display = '';
        if (navItems.menu) navItems.menu.style.display = '';

        // Customer and Employee can see: Home, Menu, Orders
        // Admin can see: Home, Menu, Orders, Dashboard (and individual mgmt links if desired)

        const isSelfService = (role === 'CUSTOMER' || role === 'EMPLOYEE' || role === 'ADMIN');
        const isAdmin = (role === 'ADMIN');

        if (navItems.orders) navItems.orders.style.display = isSelfService ? '' : 'none';

        // Admin specific
        // Show Admin Dashboard link
        if (navItems.admin) navItems.admin.style.display = isAdmin ? '' : 'none';

        // Hide direct management links from Navbar (accessed via Dashboard)
        if (navItems.stocks) navItems.stocks.style.display = 'none';
        if (navItems.products) navItems.products.style.display = 'none';
        if (navItems.users) navItems.users.style.display = 'none';

        // Client Orders for Employee (Admin sees via Dashboard) - wait user said NO kitchen orders for admin?
        // "fara kitchen orders pt admin" -> "no kitchen orders for admin"
        // So hide it completely for admin even in dashboard? Use said "remove kitchen orders for admin".
        // I removed it from dashboard HTML.
        // And from navbar logic?
        if (navItems.clientOrders) navItems.clientOrders.style.display = (role === 'EMPLOYEE') ? '' : 'none';

        // Legacy order-btn visibility if present in some pages
        const orderBtn = document.querySelector('.order-btn');
        if (orderBtn) {
            orderBtn.style.display = (role === 'CUSTOMER' || role === 'EMPLOYEE' || role === 'ADMIN') ? '' : 'none';
        }
    }

    // Listen to global auth-change
    window.addEventListener('auth-change', syncAuthButtons);
    syncAuthButtons();

})();

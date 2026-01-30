// Load navbar HTML then its script
const navbarContainer = document.getElementById("navbar");
fetch("/frontend/shared/components/navigation-bar/navigation-bar.html")
    .then(res => res.text())
    .then(html => {
        navbarContainer.innerHTML = html;
        import("/frontend/shared/components/navigation-bar/navigation-bar.js").catch(() => { });
    });

// Simple auth helper (placeholder). Replace with real auth/token logic later.
function isAuthenticated() {
    return !!localStorage.getItem("token");
}
window.isAuthenticated = isAuthenticated;

function getUserRole() {
    const userStr = localStorage.getItem("user");
    if (!userStr) return null;
    try {
        const user = JSON.parse(userStr);
        return user.role ? user.role.toUpperCase() : null;
    } catch (e) {
        return null;
    }
}
window.getUserRole = getUserRole;

// Ensure auth-change event updates basic UI if needed
window.addEventListener('auth-change', () => {
    console.log("Auth state changed, user logged in:", isAuthenticated());
});

let currentPageCss = null;
const protectedPaths = {
    "/orders": ["CUSTOMER", "EMPLOYEE", "ADMIN"],
    "/stocks": ["ADMIN"],
    "/products": ["ADMIN"],
    "/users": ["ADMIN"]
};

function navigate(path) {
    const allowedRoles = protectedPaths[path];
    if (allowedRoles) {
        const authed = isAuthenticated();
        const role = getUserRole();

        if (!authed || !allowedRoles.includes(role)) {
            // If not authorized, redirect to home or login
            const target = authed ? "/home" : "/login";
            history.pushState({}, "", target);
            loadPage(target);

            if (!authed) {
                // Optionally open auth modal if not logged in
                window.dispatchEvent(new CustomEvent('open-auth-modal'));
            }
            return;
        }
    }
    history.pushState({}, "", path);
    loadPage(path);
}

// expose navigate globally for inline onclick handlers in fetched HTML
window.navigate = navigate;

async function loadPage(path) {
    console.log("Navigating to path:", path);

    // Centralized Route Security (Frontend)
    const allowedRoles = protectedPaths[path];
    if (allowedRoles) {
        const authed = isAuthenticated();
        const role = getUserRole();

        if (!authed || !allowedRoles.includes(role)) {
            console.warn("Access denied for path:", path);
            const target = authed ? "/home" : "/login";

            // Redirect if trying to access unauthorized path
            if (path !== target) {
                history.replaceState({}, "", target);
                return loadPage(target);
            }
        }
    }

    let page;
    switch (path) {
        case "/":
        case "/home":
            page = "/frontend/modules/pages/home/home.html";
            break;
        case "/menu":
            page = "/frontend/modules/pages/menu/menu.html";
            break;
        case "/login":
            page = "/frontend/modules/pages/login/login.html";
            break;
        case "/orders":
            page = "/frontend/modules/pages/orders/orders.html";
            break;
        case "/stocks":
            page = "/frontend/modules/pages/stock_page/stock_page.html";
            break;
        case "/users":
            page = "/frontend/modules/pages/user_manager/user_manager.html";
            break;
        case "/products":
            page = "/frontend/modules/pages/product_manager/product_manager.html";
            break;
        default:
            page = "/frontend/modules/pages/home/home.html";
            break;
    }

    try {
        const res = await fetch(page);
        const html = await res.text();
        document.getElementById("app").innerHTML = html;
    } catch (e) {
        document.getElementById("app").innerHTML = "<p>Failed to load page.</p>";
        return;
    }

    // Try to load page-specific CSS (same folder, same name .css)
    const cssPath = page.replace('.html', '.css');
    await loadPageCss(cssPath);

    // Try to import page-specific JS as module
    const jsPath = page.replace('.html', '.js');
    try {
        const module = await import(jsPath);
        if (module && typeof module.init === 'function') {
            module.init();
        }
    } catch (e) {
        // ignore missing page script
    }
}

async function loadPageCss(href) {
    if (currentPageCss) {
        currentPageCss.remove();
        currentPageCss = null;
    }

    try {
        const res = await fetch(href, { method: 'GET' });
        if (!res.ok) return;
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = href;
        document.head.appendChild(link);
        currentPageCss = link;
    } catch (e) {
        // no css for page
    }
}

// Handle back/forward
window.onpopstate = () => loadPage(location.pathname || '/');

// Initial load
loadPage(location.pathname || '/');

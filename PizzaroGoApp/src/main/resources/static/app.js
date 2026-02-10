// Load navbar HTML then its script
const navbarContainer = document.getElementById("navbar");
fetch("/frontend/shared/components/navigation-bar/navigation-bar.html")
    .then(res => res.text())
    .then(html => {
        navbarContainer.innerHTML = html;
        import("/frontend/shared/components/navigation-bar/navigation-bar.js").catch(() => { });
    });

// State object for current user
let currentUser = null;

async function refreshSession() {
    try {
        const { userService } = await import("/frontend/domains/user/UserService.js");
        currentUser = await userService.me();
        return currentUser;
    } catch (e) {
        console.error("Failed to refresh session", e);
        return null;
    }
}

// Auth helpers
async function isAuthenticated() {
    const user = await refreshSession();
    return !!user;
}
window.isAuthenticatedAsync = isAuthenticated;

// For synchronous checks where we already have the state
window.isAuthenticated = () => !!currentUser;

function getUserRole() {
    if (!currentUser) return null;
    return currentUser.role ? currentUser.role.toUpperCase() : null;
}
window.getUserRole = getUserRole;

// Ensure auth-change event updates basic UI
window.addEventListener('auth-change', async () => {
    await refreshSession();
    console.log("Auth state changed, user logged in:", !!currentUser);
});

let currentPageCss = null;
const protectedPaths = {
    "/orders": ["CUSTOMER", "EMPLOYEE", "ADMIN"],
    "/stocks": ["ADMIN"],
    "/products": ["ADMIN"],
    "/users": ["ADMIN"],
    "/client-orders": ["EMPLOYEE", "ADMIN"],
    "/admin": ["ADMIN"]
};

async function navigate(path) {
    const allowedRoles = protectedPaths[path];
    if (allowedRoles) {
        const user = await refreshSession();
        const authed = !!user;
        const role = getUserRole();

        if (!authed || !allowedRoles.includes(role)) {
            const target = authed ? "/home" : "/login";
            history.pushState({}, "", target);
            loadPage(target);

            if (!authed) {
                window.dispatchEvent(new CustomEvent('open-auth-modal'));
            }
            return;
        }
    }
    history.pushState({}, "", path);
    loadPage(path);
}
window.navigate = navigate;

async function loadPage(path) {
    console.log("Navigating to path:", path);

    // Initial session check before loading protected page
    await refreshSession();

    const allowedRoles = protectedPaths[path];
    if (allowedRoles) {
        const authed = !!currentUser;
        const role = getUserRole();

        if (!authed || !allowedRoles.includes(role)) {
            console.warn("Access denied for path:", path);
            const target = authed ? "/home" : "/login";

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
        case "/client-orders":
            page = "/frontend/modules/pages/client_orders/client_orders.html";
            break;
        case "/admin":
            page = "/frontend/modules/pages/admin_dashboard/admin_dashboard.html";
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

    const cssPath = page.replace('.html', '.css');
    await loadPageCss(cssPath);

    const jsPath = page.replace('.html', '.js');
    try {
        const module = await import(jsPath);
        if (module && typeof module.init === 'function') {
            module.init();
        }
    } catch (e) { }
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
    } catch (e) { }
}

window.onpopstate = () => loadPage(location.pathname || '/');

// Initial load
loadPage(location.pathname || '/');

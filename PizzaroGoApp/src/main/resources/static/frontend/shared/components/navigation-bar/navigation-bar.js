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

    // 4. Update sync logic for other buttons if any
    function syncAuthButtons() {
        // order-btn visibility might still need to depend on auth
        const orderBtn = document.querySelector('.order-btn');
        if (!orderBtn) return;

        const authed = window.isAuthenticated ? window.isAuthenticated() : false;
        if (authed) {
            orderBtn.style.display = '';
        } else {
            orderBtn.style.display = 'none';
        }
    }

    // Listen to global auth-change
    window.addEventListener('auth-change', syncAuthButtons);
    syncAuthButtons();

})();

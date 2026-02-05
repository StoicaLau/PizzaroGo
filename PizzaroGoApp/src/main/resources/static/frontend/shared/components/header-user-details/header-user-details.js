(function () {
    const componentId = 'header-user-details';

    class HeaderUserDetails {
        constructor() {
            this.init();
        }

        async init() {
            // Wait for DOM to be ready if needed, but since we are imported, it might be safer to wait or check
            // However, usually this script runs after the HTML is injected if we handle it that way.
            // For now, we assume the HTML is already in the DOM or will be injected.
            // But wait! The current app.js inserts navbar HTML, which should contain this component's placeholder.
            // We need to attach listeners to *existing* elements.

            // Re-query elements in case of re-renders
            this.updateReferences();
            this.bindEvents();
            this.checkAuthState();

            // Listen for global auth changes
            window.addEventListener('auth-change', () => this.checkAuthState());

            // Listen for cart changes
            window.addEventListener('cart-change', (e) => this.updateCartBadge(e.detail));

            // Close dropdown when clicking outside
            document.addEventListener('click', (e) => {
                if (this.profileTrigger && !this.profileTrigger.contains(e.target) &&
                    this.dropdown && !this.dropdown.contains(e.target)) {
                    this.dropdown.classList.add('hidden');
                    this.updateArrow(false);
                }
            });
        }

        updateReferences() {
            this.container = document.querySelector('.header-user-details');
            this.unauthSection = document.getElementById('hud-unauth');
            this.authSection = document.getElementById('hud-auth');
            this.loginBtn = document.getElementById('hud-login-btn');
            this.profileTrigger = document.getElementById('hud-profile-trigger');
            this.dropdown = document.getElementById('hud-dropdown');
            this.usernameLabel = document.getElementById('hud-username');
            this.emailLabel = document.getElementById('hud-email');
            this.logoutBtn = document.getElementById('hud-logout-btn');
            this.arrow = document.querySelector('.dropdown-icon .arrow');

            this.cartBtn = document.getElementById('hud-cart-btn');
            this.cartCount = document.getElementById('hud-cart-count');
        }

        bindEvents() {
            if (this.loginBtn) {
                this.loginBtn.onclick = () => {
                    // Dispatch event to open Auth Modal
                    window.dispatchEvent(new CustomEvent('open-auth-modal'));
                };
            }

            if (this.profileTrigger) {
                this.profileTrigger.onclick = () => {
                    this.toggleDropdown();
                };
            }

            if (this.logoutBtn) {
                this.logoutBtn.onclick = () => {
                    this.logout();
                };
            }

            if (this.cartBtn) {
                this.cartBtn.onclick = () => {
                    const currentPath = window.location.hash || window.location.pathname;
                    if (currentPath.includes('menu')) {
                        // We are already on menu page, just open it (menu.js will handle its own listener, but we can trigger it)
                        const openCartEvent = new CustomEvent('open-menu-cart');
                        window.dispatchEvent(openCartEvent);
                    } else if (window.navigate) {
                        localStorage.setItem('open_cart_on_load', 'true');
                        window.navigate('/menu');
                    }
                };
            }
        }

        toggleDropdown() {
            if (!this.dropdown) return;
            const isHidden = this.dropdown.classList.contains('hidden');
            if (isHidden) {
                this.dropdown.classList.remove('hidden');
                this.updateArrow(true);
            } else {
                this.dropdown.classList.add('hidden');
                this.updateArrow(false);
            }
        }

        updateArrow(isOpen) {
            if (this.arrow) {
                if (isOpen) {
                    this.arrow.classList.remove('down');
                    this.arrow.classList.add('up');
                } else {
                    this.arrow.classList.remove('up');
                    this.arrow.classList.add('down');
                }
            }
        }

        updateCartBadge(items) {
            if (!this.cartCount) return;
            const count = items.reduce((sum, item) => sum + item.quantity, 0);
            this.cartCount.textContent = count;
            this.cartCount.classList.toggle('hidden', count === 0);
        }

        checkAuthState() {
            const user = JSON.parse(localStorage.getItem('user') || 'null');
            const isLoggedIn = !!user;

            this.updateReferences();

            if (isLoggedIn) {
                if (this.unauthSection) this.unauthSection.classList.add('hidden');
                if (this.authSection) this.authSection.classList.remove('hidden');
                if (this.usernameLabel) this.usernameLabel.textContent = user.username || 'User';
                if (this.emailLabel) this.emailLabel.textContent = user.email || '';

                // Initialize cart badge from storage
                const cartData = localStorage.getItem('pizzarogo_cart');
                const items = cartData ? JSON.parse(cartData) : [];
                this.updateCartBadge(items);

            } else {
                if (this.unauthSection) this.unauthSection.classList.remove('hidden');
                if (this.authSection) this.authSection.classList.add('hidden');
            }
        }

        logout() {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            // Clear cart on logout
            localStorage.removeItem('pizzarogo_cart');
            window.dispatchEvent(new CustomEvent('cart-change', { detail: [] }));
            window.dispatchEvent(new CustomEvent('auth-change'));
            if (window.navigate) window.navigate('/');
        }
    }

    // Initialize
    new HeaderUserDetails();
})();

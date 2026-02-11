(function () {
    const componentId = 'header-user-details';

    class HeaderUserDetails {
        constructor() {
            this.init();
        }

        async init() {
            // Dynamic import for UserService
            try {
                const { userService } = await import('/frontend/domains/user/UserService.js');
                this.userService = userService;
                await this.syncSession();
            } catch (e) {
                console.error("Hud: Failed to load UserService", e);
            }

            this.updateReferences();
            this.bindEvents();
            await this.checkAuthState();

            // Listen for global auth changes (e.g. after login/logout)
            window.addEventListener('auth-change', async () => {
                if (this.userService) {
                    // Force refresh internal state in service
                    this.userService._currentUser = null;
                }
                await this.checkAuthState();
            });

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

        async syncSession() {
            if (!this.userService) return;
            try {
                // me() now caches in memory, so this is safe to call
                await this.userService.me();
            } catch (e) {
                console.error("Hud: Session sync failed", e);
            }
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

        async checkAuthState() {
            let user = null;
            if (this.userService) {
                user = await this.userService.me();
            }

            const isLoggedIn = !!user;
            this.updateReferences();

            if (isLoggedIn) {
                if (this.unauthSection) this.unauthSection.classList.add('hidden');
                if (this.authSection) this.authSection.classList.remove('hidden');
                if (this.usernameLabel) this.usernameLabel.textContent = user.username || 'User';
                if (this.emailLabel) this.emailLabel.textContent = user.email || '';

                const cartData = localStorage.getItem('pizzarogo_cart');
                const items = cartData ? JSON.parse(cartData) : [];
                this.updateCartBadge(items);
            } else {
                if (this.unauthSection) this.unauthSection.classList.remove('hidden');
                if (this.authSection) this.authSection.classList.add('hidden');
            }
        }

        async logout() {
            try {
                if (this.userService) {
                    this.userService.logout();
                }
            } catch (e) { }

            // Remove only NON-SENSITIVE local cache like cart (optional)
            localStorage.removeItem('pizzarogo_cart');
            window.dispatchEvent(new CustomEvent('cart-change', { detail: [] }));
            window.dispatchEvent(new CustomEvent('auth-change'));
            window.location.href = '/';
        }
    }

    // Initialize
    new HeaderUserDetails();
})();

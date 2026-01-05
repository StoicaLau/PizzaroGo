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
                    // Start navigation to orders or cart
                    // checking if navigate function exists globaly or dispatch event
                    if (window.navigate) {
                        window.navigate('/orders');
                    } else {
                        console.log("Navigating to orders...");
                        window.location.href = '/orders'; // Fallback
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

        checkAuthState() {
            // Assume window.isAuthenticated() and window.getUser() are available
            // If checking from localStorage directly:
            const user = JSON.parse(localStorage.getItem('user') || 'null');
            const isLoggedIn = !!user;

            this.updateReferences(); // Ensure we have latest DOM

            if (isLoggedIn) {
                if (this.unauthSection) this.unauthSection.classList.add('hidden');
                if (this.authSection) this.authSection.classList.remove('hidden');
                if (this.usernameLabel) this.usernameLabel.textContent = user.username || 'User';
                if (this.emailLabel) this.emailLabel.textContent = user.email || '';

                // Update cart count if user has orders data (mocked for now)
                if (this.cartCount) {
                    // In a real app, fetch cart size from API or local storage
                    // For now, let's show a mock number like 2 if logged in, or 0
                    const mockCount = user.orders ? user.orders.length : 2;
                    this.cartCount.textContent = mockCount;
                }

            } else {
                if (this.unauthSection) this.unauthSection.classList.remove('hidden');
                if (this.authSection) this.authSection.classList.add('hidden');
            }
        }

        logout() {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            window.dispatchEvent(new CustomEvent('auth-change'));
            // Optional: Redirect to home
            if (window.navigate) window.navigate('/');
        }
    }

    // Initialize
    new HeaderUserDetails();
})();

(function () {
    class UserAuthentication {
        constructor() {
            this.init();
        }

        async init() {
            this.bindEvents();
            this.listenForGlobalEvents();
            await this.loadSubComponents();
        }

        async loadSubComponents() {
            // Load Login Component
            try {
                const loginRes = await fetch('/frontend/modules/components/user-login/user-login.html');
                if (loginRes.ok) {
                    const html = await loginRes.text();
                    const container = document.getElementById('user-login-placeholder');
                    if (container) {
                        container.innerHTML = html;
                        // Load CSS
                        this.loadCss('/frontend/modules/components/user-login/user-login.css');
                        // Load JS
                        import('/frontend/modules/components/user-login/user-login.js').catch(e => console.warn(e));
                    }
                }
            } catch (e) { console.error('Failed to load login component', e); }

            // Load Register Component
            try {
                const regRes = await fetch('/frontend/modules/components/user-register/user-register.html');
                if (regRes.ok) {
                    const html = await regRes.text();
                    const container = document.getElementById('user-register-placeholder');
                    if (container) {
                        container.innerHTML = html;
                        // Load CSS
                        this.loadCss('/frontend/modules/components/user-register/user-register.css');
                        // Load JS
                        import('/frontend/modules/components/user-register/user-register.js').catch(e => console.warn(e));
                    }
                }
            } catch (e) { console.error('Failed to load register component', e); }
        }

        loadCss(href) {
            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = href;
            document.head.appendChild(link);
        }

        updateReferences() {
            this.modal = document.getElementById('auth-modal');
            this.closeBtn = document.getElementById('auth-close-btn');
            this.tabLogin = document.getElementById('tab-login');
            this.tabRegister = document.getElementById('tab-register');
            this.viewLogin = document.getElementById('view-login');
            this.viewRegister = document.getElementById('view-register');
        }

        bindEvents() {
            this.updateReferences();

            if (this.closeBtn) {
                this.closeBtn.onclick = () => this.closeModal();
            }

            if (this.tabLogin) {
                this.tabLogin.onclick = () => this.switchTab('login');
            }

            if (this.tabRegister) {
                this.tabRegister.onclick = () => this.switchTab('register');
            }

            // Close on backdrop click
            if (this.modal) {
                this.modal.onclick = (e) => {
                    if (e.target === this.modal) {
                        this.closeModal();
                    }
                };
            }
        }

        listenForGlobalEvents() {
            window.addEventListener('open-auth-modal', () => this.openModal());

            // Listen for internal switches (e.g. from register to login)
            window.addEventListener('switch-to-login', () => this.switchTab('login'));
            window.addEventListener('switch-to-register', () => this.switchTab('register'));
        }

        openModal() {
            this.updateReferences();
            if (this.modal) {
                this.modal.classList.remove('hidden');
                // document.body.style.overflow = 'hidden'; // Prevent scrolling
            }
        }

        closeModal() {
            if (this.modal) {
                this.modal.classList.add('hidden');
                // document.body.style.overflow = '';
            }
        }

        switchTab(tab) {
            this.updateReferences();
            if (tab === 'login') {
                this.tabLogin.classList.add('active');
                this.tabRegister.classList.remove('active');
                this.viewLogin.classList.remove('hidden');
                this.viewRegister.classList.add('hidden');
            } else {
                this.tabRegister.classList.add('active');
                this.tabLogin.classList.remove('active');
                this.viewRegister.classList.remove('hidden');
                this.viewLogin.classList.add('hidden');
            }
        }
    }

    new UserAuthentication();
})();

(async function () {
    const btn = document.getElementById('btn-submit-register');
    const usernameInput = document.getElementById('reg-username');
    const emailInput = document.getElementById('reg-email');
    const phoneInput = document.getElementById('reg-phone');
    const passwordInput = document.getElementById('reg-password');
    const linkLogin = document.getElementById('link-to-login');

    // Dynamic import for UserService and User
    let UserService, User;
    try {
        const userModule = await import('/frontend/domains/user/User.js');
        const serviceModule = await import('/frontend/domains/user/UserService.js');
        User = userModule.User;
        UserService = serviceModule.UserService;
    } catch (e) {
        console.error("Failed to load User modules", e);
    }

    const userService = new UserService();

    const messageContainer = document.getElementById('register-message');

    function showMessage(message, type) {
        if (!messageContainer) return;
        messageContainer.textContent = message;
        messageContainer.className = `message-container ${type}`;
    }

    function hideMessage() {
        if (!messageContainer) return;
        messageContainer.className = 'message-container hidden';
        messageContainer.textContent = '';
    }

    if (btn) {
        btn.onclick = async () => {
            hideMessage();

            if (!User || !UserService) {
                showMessage("System error: Modules not loaded.", "error");
                return;
            }

            const username = usernameInput ? usernameInput.value : '';
            const email = emailInput ? emailInput.value : '';
            const phone = phoneInput ? phoneInput.value : '';
            const password = passwordInput ? passwordInput.value : '';

            if (!username || !email || !password || !phone) {
                showMessage("Please fill in all fields.", "error");
                return;
            }

            try {
                const newUser = new User(username, email, phone, password);
                const response = await userService.register(newUser);

                showMessage("Registration successful! Redirecting to login...", "success");
                console.log("Registration Response:", response);

                // Switch to login tab after delay
                setTimeout(() => {
                    window.dispatchEvent(new CustomEvent('switch-to-login'));
                    // Reset form
                    if (usernameInput) usernameInput.value = '';
                    if (emailInput) emailInput.value = '';
                    if (phoneInput) phoneInput.value = '';
                    if (passwordInput) passwordInput.value = '';
                    hideMessage();
                }, 1500);

            } catch (error) {
                console.error("Registration failed:", error);
                showMessage(error.message || "Registration failed.", "error");
            }
        };
    }

    if (linkLogin) {
        linkLogin.onclick = (e) => {
            e.preventDefault();
            hideMessage();
            window.dispatchEvent(new CustomEvent('switch-to-login'));
        };
    }
})();

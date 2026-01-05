(async function () {
    const btn = document.getElementById('btn-submit-login');
    const emailInput = document.getElementById('login-email');
    const passwordInput = document.getElementById('login-password');

    // Dynamic import for UserService and User
    let UserService;
    try {
        const serviceModule = await import('/frontend/domains/user/UserService.js');
        UserService = serviceModule.UserService;
    } catch (e) {
        console.error("Failed to load User modules", e);
    }

    const userService = new UserService();

    const messageContainer = document.getElementById('login-message');

    function showMessage(message, type) {
        if (!messageContainer) return;
        messageContainer.textContent = message;
        messageContainer.className = `message-container ${type}`;
        // Removing 'hidden' class to show
    }

    function hideMessage() {
        if (!messageContainer) return;
        messageContainer.className = 'message-container hidden';
        messageContainer.textContent = '';
    }

    if (btn) {
        btn.onclick = async () => {
            hideMessage();

            if (!UserService) {
                showMessage("System error: Modules not loaded.", "error");
                return;
            }

            const email = emailInput ? emailInput.value : '';
            const password = passwordInput ? passwordInput.value : '';

            if (!email || !password) {
                showMessage("Please enter email and password.", "error");
                return;
            }

            try {
                console.log("Logging in...", email);
                const user = await userService.login(email, password);

                // Store user and token
                localStorage.setItem('user', JSON.stringify(user));
                localStorage.setItem('token', 'valid-token-' + Date.now());

                window.dispatchEvent(new CustomEvent('auth-change'));

                // Show success briefly or close immediately? 
                // Usually we just close or show success then close.
                showMessage("Login successful!", "success");

                setTimeout(() => {
                    const modal = document.getElementById('auth-modal');
                    if (modal) modal.classList.add('hidden');
                    // Reset form
                    if (emailInput) emailInput.value = '';
                    if (passwordInput) passwordInput.value = '';
                    hideMessage();
                }, 1000);

            } catch (error) {
                console.error("Login failed:", error);
                showMessage(error.message || "Login failed.", "error");
            }
        };
    }
})();

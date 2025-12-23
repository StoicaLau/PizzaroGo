document.addEventListener("click", e => {
    if (e.target.classList.contains("login-btn")) {
        navigate("/login");
    }

    if (e.target.classList.contains("order-btn")) {
        navigate("/menu");
    }
});
// Toggle login/order visibility based on auth state (app exposes `isAuthenticated`)
function syncAuthButtons() {
    const loginBtn = document.querySelector('.login-btn');
    const orderBtn = document.querySelector('.order-btn');
    if (!loginBtn || !orderBtn) return;
    try {
        const authed = window.isAuthenticated ? window.isAuthenticated() : false;
        if (authed) {
            loginBtn.style.display = 'none';
            orderBtn.style.display = '';
        } else {
            loginBtn.style.display = '';
            orderBtn.style.display = 'none';
        }
    } catch (e) {
        loginBtn.style.display = '';
        orderBtn.style.display = 'none';
    }
}

// run on load and when custom event `auth-changed` is dispatched
document.addEventListener('DOMContentLoaded', syncAuthButtons);
syncAuthButtons();

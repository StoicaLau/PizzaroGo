document.addEventListener("click", e => {
    if (e.target.classList.contains("login-btn")) {
        navigate("/login");
    }

    if (e.target.classList.contains("order-btn")) {
        navigate("/menu");
    }
});

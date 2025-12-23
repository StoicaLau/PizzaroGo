document.addEventListener("click", e => {
    if (e.target.classList.contains("primary")) {
        navigate("/menu");
    }

    if (e.target.classList.contains("secondary")) {
        navigate("/login");
    }
});

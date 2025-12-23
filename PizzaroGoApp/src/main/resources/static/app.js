// Load navbar
fetch("/frontend/shared/components/navigation-bar/navigation-bar.html")
  .then(res => res.text())
  .then(html => document.getElementById("navbar").innerHTML = html);

function navigate(path) {
    history.pushState({}, "", path);
    loadPage(path);
}

function loadPage(path) {
    console.log("Navigating to path:", path);
    let page;
    switch (path) {
        case "/":
        case "/home":
            page = "/frontend/modules/pages/home/home.html";
            break;
        case "/menu":
            page = "/frontend/modules/pages/menu/menu.html";
            break;
        case "/login":
            page = "/frontend/modules/pages/login/login.html";
            break;
        default:
            page = "/frontend/modules/pages/home/home.html";
            break;
    }
    fetch(page)
    .then(res => res.text())
    .then(html => {
    document.getElementById("app").innerHTML = html;

    // Optional: load JS per page
    if (path === "/home") import("/frontend/modules/pages/home/home.js");
    if (path === "/menu") import("/frontend/modules/pages/menu/menu.js");
    if (path === "/login") import("/frontend/modules/pages/login/login.js");
});
}

// Handle browser back/forward
window.onpopstate = () => loadPage(location.pathname);

// Initial load
loadPage(location.pathname);

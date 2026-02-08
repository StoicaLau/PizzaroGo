package com.pizzaro_go.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class FrontendController {

    @GetMapping({ "/", "/home", "/menu", "/login", "/orders", "/stocks", "/users", "/products", "/client-orders" })
    public String index() {
        return "forward:/index.html";
    }
}

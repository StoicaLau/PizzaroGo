package com.pizzaro_go.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({"/", "/home", "/menu", "/login"})
    public String index() {
        return "forward:/index.html";
    }
}

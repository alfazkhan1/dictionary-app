package com.dictionary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/admin")
    public String adminPage() {
        return "forward:/admin.html";
    }

    @GetMapping("/member")
    public String memberPage() {
        return "forward:/member.html";
    }
    @GetMapping("/ping")
    public String ping(){
        return "OK";
    }
}
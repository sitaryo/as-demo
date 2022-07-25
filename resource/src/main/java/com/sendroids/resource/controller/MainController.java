package com.sendroids.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/message")
    public String message() {
        return "here is message in resource server";
    }
}

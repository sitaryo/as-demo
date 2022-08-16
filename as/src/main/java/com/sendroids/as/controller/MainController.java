package com.sendroids.as.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/message")
    @PreAuthorize("hasAuthority('SCOPE_read')")
    public String message() {
        return "here is message in resource server";
    }
}

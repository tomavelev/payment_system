package com.tomavelev.payment.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello from public endpoint!";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello from user endpoint!";
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello from admin endpoint!";
    }
}

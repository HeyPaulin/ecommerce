package com.example.JWT_RestAPI.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/users")
    public String manageUsers(@RequestBody String userData, Authentication authentication) {
        return "Admin " + authentication.getName() + " managing users: " + userData;
    }

    @Secured("ROLE_GERENTE")
    @PostMapping("/manager/products")
    public String manageProducts(@RequestBody String productData, Authentication authentication) {
        return "Gerente " + authentication.getName() + " managing products: " + productData;
    }

    @Secured("ROLE_VENDEDOR")
    @PostMapping("/seller/orders")
    public String manageOrders(@RequestBody String orderData, Authentication authentication) {
        return "Vendedor " + authentication.getName() + " managing orders: " + orderData;
    }

    @Secured("ROLE_CLIENTE")
    @GetMapping("/customer/products")
    public String viewProducts(Authentication authentication) {
        return "Cliente " + authentication.getName() + " viewing products";
    }
}

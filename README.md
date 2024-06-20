# JWT Rest API

## Descrição do Serviço

Este serviço é uma API RESTful JWT com autenticação e autorização para diferentes tipos de usuários (administradores, gerentes de produtos, vendedores, e clientes). Cada tipo de usuário tem acesso a diferentes endpoints para gerenciar usuários, produtos, pedidos, e acessar o catálogo de produtos.


## Diagrama de Arquitetura

<![detailed_microservices_diagram png](https://github.com/HeyPaulin/ecommerce/assets/101124585/d5c97c68-742c-4282-bb5a-77992e7c2e14) alt="Diagrama Detalhado de Arquitetura dos Microsserviços">

## Estrutura do Código

### 1. JwtRestApiApplication

Arquivo: `JwtRestApiApplication.java`

Responsável por inicializar a aplicação Spring Boot.

```java
package com.example.JWT_RestAPI.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example"})
public class JwtRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtRestApiApplication.class, args);
    }
}
 2. SecurityConfig
Arquivo: SecurityConfig.java

Configura as permissões de acesso aos endpoints baseados nos papéis dos usuários.

java

package com.example.JWT_RestAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/username/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/manager/products").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.POST, "/seller/orders").hasRole("VENDEDOR")
                        .requestMatchers(HttpMethod.GET, "/customer/products").hasRole("CLIENTE")
                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("bruno")
                .password(passwordEncoder().encode("4321"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("fernando")
                .password(passwordEncoder().encode("5555"))
                .roles("ADMIN")
                .build();
        UserDetails gerente = User.builder()
                .username("julia")
                .password(passwordEncoder().encode("1234"))
                .roles("GERENTE")
                .build();
        UserDetails cliente = User.builder()
                .username("junior")
                .password(passwordEncoder().encode("7890"))
                .roles("CLIENTE")
                .build();
        UserDetails vendedor = User.builder()
                .username("carol")
                .password(passwordEncoder().encode("4444"))
                .roles("VENDEDOR")
                .build();
        return new InMemoryUserDetailsManager(user, admin, gerente, cliente, vendedor);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
3. AuthController
Arquivo: AuthController.java

Controlador responsável por autenticação e extração de informações de usuários.

java
Copiar código
package com.example.JWT_RestAPI.controller;

import com.example.JWT_RestAPI.model.LoginRequest;
import com.example.JWT_RestAPI.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        String token = authService.generateToken(request.getUsername());
        return token;
    }

    @GetMapping("/username/{token}")
    public String extractUsername(@PathVariable String token) {
        String username = authService.extractUsername(token);
        return username;
    }

    @GetMapping("/user")
    public String getUser(Authentication authentication) {
        return "User: " + authentication.getName();
    }
}
4. ManagementController
Arquivo: ManagementController.java

Controlador responsável pelo gerenciamento de usuários, produtos, pedidos e acesso ao catálogo de produtos.

java
Copiar código
package com.example.JWT_RestAPI.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class ManagementController {

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
5. LoginRequest
Arquivo: LoginRequest.java

Modelo de dados para requisições de login.

java
Copiar código
package com.example.JWT_RestAPI.model;

public class LoginRequest {
    private String username;
    private String password;
    private String email;

    public LoginRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
6. JwtUtil
Arquivo: JwtUtil.java

Utilitário para gerar e extrair informações de tokens JWT.

java
Copiar código
package com.example.JWT_RestAPI.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final SecretKey SECRET_KEY = generateSecretKey();
    private static final String SECRET_STRING = getSecretString();
    private static final long EXPIRATION_TIME = 864_000_000;

    private static SecretKey generateSecretKey() {
        SecretKey key = Jwts.SIG.HS512.key().build();
        return key;
    }

    private static String getSecretString() {
        String secretString = Encoders.BASE64.encode(SECRET_KEY.getEncoded());
        System.out.println("Secret Key: " + secretString);
        return secretString;
    }

    public static String generateToken(String username) {
        String token = Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        System.out.println("Token: " + token);
        return token;
    }

    public static String extractUsername(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_STRING));
        return Jwts.parser().verifyWith(secret).build().parseSignedClaims(token)
                .getPayload().getSubject();
    }
}
7. AuthService
Arquivo: AuthService.java

Serviço de autenticação para geração e extração de informações dos tokens JWT.

java
Copiar código
package com.example.JWT_RestAPI.service;

import com.example.JWT_RestAPI.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public String generateToken(String username) {
        String token = JwtUtil.generateToken(username);
        return token;
    }

    public String extractUsername(String token) {
        String username = JwtUtil.extractUsername(token);
        return username;
    }
}
Endpoints
AuthController:
POST /login: Gera um token JWT para o usuário.
GET /username/{token}: Extrai o nome de usuário do token JWT.

![login](https://github.com/HeyPaulin/ecommerce/assets/101124585/c2dabf7c-d37e-4a54-8a6b-d540b76de6ca)

![extract username](https://github.com/HeyPaulin/ecommerce/assets/101124585/d8c528a8-7a29-42df-b451-9104221775c4)

![Admin](https://github.com/HeyPaulin/ecommerce/assets/101124585/8dead2ab-a1e3-4a1f-be54-f77e9240d7ca)

![Gerente](https://github.com/HeyPaulin/ecommerce/assets/101124585/c393d9a6-bfd5-4bcf-95ce-1459cdfc3422)

![Cliente](https://github.com/HeyPaulin/ecommerce/assets/101124585/03ce10ed-470f-4fe7-8cc9-31304ff9980c)

![Vendedor](https://github.com/HeyPaulin/ecommerce/assets/101124585/661738ec-eef2-4b48-903d-51ae638d0a7d)

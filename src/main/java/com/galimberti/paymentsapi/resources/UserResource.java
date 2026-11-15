package com.galimberti.paymentsapi.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galimberti.paymentsapi.config.JwtProperties;
import com.galimberti.paymentsapi.domain.User;
import com.galimberti.paymentsapi.dto.LoginRequest;
import com.galimberti.paymentsapi.dto.RegisterRequest;
import com.galimberti.paymentsapi.services.UserService;

import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Usuarios", description = "Cadastro e autenticacao")
@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired 
    UserService userService;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @Operation(summary = "Executa o login")
    @ApiResponse(responseCode = "200", description = "token JWT")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos", content = @Content)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginRequest body) {
        User user = userService.validateUser(body.email(), body.password());
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    @Operation(summary = "Cria um usuario")
    @ApiResponse(responseCode = "200", description = "Usuario criado")
    @ApiResponse(responseCode = "401", description = "e-mail em formato inválido, e-mail já cadastrado ou falha ao criar a conta", content = @Content)
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest body) {
        User user = userService.registerUser(body.firstName(), body.lastName(), body.email(), body.password());
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    private Map<String, String> generateJWTToken(User user){

        long timestamp = System.currentTimeMillis();

        String token = Jwts.builder()
            .issuedAt(new Date(timestamp))
            .expiration(new Date(timestamp + jwtProperties.validityMs()))

            .claim("userId", user.getUserId())
            .claim("email", user.getEmail())
            .claim("firstName", user.getFirstName())
            .claim("lastName", user.getLastName())
            .signWith(jwtSigningKey, Jwts.SIG.HS256)
            .compact();

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        
        return map;
    }

}

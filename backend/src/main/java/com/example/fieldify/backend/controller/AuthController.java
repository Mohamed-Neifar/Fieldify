package com.example.fieldify.backend.controller;

import com.example.fieldify.backend.dto.*;
import com.example.fieldify.backend.exception.*;
import com.example.fieldify.backend.security.JwtService;
import com.example.fieldify.backend.service.AuthService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;
import java.util.UUID;

public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            UserResponse response = authService.register(req);
            ctx.status(HttpStatus.CREATED).json(response);
        } catch (ValidationException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse(e.getMessage()));
        } catch (DuplicateEmailException e) {
            ctx.status(HttpStatus.CONFLICT).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Erreur interne du serveur"));
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            ctx.status(HttpStatus.OK).json(authService.login(req));
        } catch (ValidationException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse(e.getMessage()));
        } catch (InvalidCredentialsException e) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Erreur interne du serveur"));
        }
    }

    public void changePassword(Context ctx) {
        try {
            ChangePasswordRequest req = ctx.bodyAsClass(ChangePasswordRequest.class);
            authService.changePassword(req);
            ctx.status(HttpStatus.OK).json(Map.of("message", "Mot de passe modifié avec succès"));
        } catch (ValidationException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse(e.getMessage()));
        } catch (InvalidCredentialsException e) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Erreur interne du serveur"));
        }
    }

    public void getCurrentUser(Context ctx) {
        try {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Jeton de sécurité manquant ou invalide"));
                return;
            }

            String token = authHeader.substring(7).trim();
            UUID userId = jwtService.getUserIdFromToken(token);
            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse("Jeton invalide ou expiré"));
                return;
            }

            UserResponse userResponse = authService.getCurrentUser(userId);
            ctx.status(HttpStatus.OK).json(userResponse);
        } catch (InvalidCredentialsException e) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Erreur interne du serveur"));
        }
    }
}

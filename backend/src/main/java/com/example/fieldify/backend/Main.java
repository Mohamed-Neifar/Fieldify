package com.example.fieldify.backend;

import com.example.fieldify.backend.config.AppConfig;
import com.example.fieldify.backend.config.DatabaseConfig;
import com.example.fieldify.backend.controller.AuthController;
import com.example.fieldify.backend.repository.UserRepository;
import com.example.fieldify.backend.security.JwtService;
import com.example.fieldify.backend.security.PasswordService;
import com.example.fieldify.backend.service.AuthService;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Fieldify Backend Application...");

        AppConfig appConfig = new AppConfig();
        DatabaseConfig databaseConfig = new DatabaseConfig(appConfig);
        databaseConfig.initDatabase();

        UserRepository userRepository = new UserRepository(databaseConfig.getDataSource());
        PasswordService passwordService = new PasswordService();
        JwtService jwtService = new JwtService(appConfig);
        AuthService authService = new AuthService(userRepository, passwordService, jwtService);
        AuthController authController = new AuthController(authService, jwtService);

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                // CORS enabled with default settings
            });
        });

        app.post("/api/auth/register", authController::register);
        app.post("/api/auth/login", authController::login);
        app.post("/api/auth/change-password", authController::changePassword);
        app.get("/api/auth/me", authController::getCurrentUser);

        app.start(appConfig.getServerPort());
        System.out.println("Fieldify Backend running on port " + appConfig.getServerPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down backend...");
            app.stop();
            databaseConfig.close();
        }));
    }
}

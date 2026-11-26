package com.example.fieldify.backend.service;

import com.example.fieldify.backend.dto.*;
import com.example.fieldify.backend.exception.*;
import com.example.fieldify.backend.model.Role;
import com.example.fieldify.backend.model.User;
import com.example.fieldify.backend.repository.UserRepository;
import com.example.fieldify.backend.security.JwtService;
import com.example.fieldify.backend.security.PasswordService;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (request == null) throw new ValidationException("Request body is required");

        String fullName = request.getFullName() != null ? request.getFullName().trim() : "";
        String rawEmail = request.getEmail() != null ? request.getEmail().trim() : "";
        String phone = request.getPhone() != null ? request.getPhone().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        if (fullName.isEmpty() || rawEmail.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            throw new ValidationException("Tous les champs sont obligatoires");
        }

        if (!EMAIL_PATTERN.matcher(rawEmail).matches()) {
            throw new ValidationException("Format d'email invalide");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Le mot de passe doit contenir au moins 8 caractères");
        }

        String normalizedEmail = rawEmail.toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("Cet email est déjà utilisé");
        }

        String passwordHash = passwordService.hashPassword(password);

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFullName(fullName);
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(passwordHash);
        newUser.setPhone(phone);
        newUser.setRole(Role.PLAYER);
        newUser.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(newUser);
        return UserResponse.fromUser(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) throw new ValidationException("Request body is required");

        String rawEmail = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        if (rawEmail.isEmpty() || password.isEmpty()) {
            throw new ValidationException("Email et mot de passe requis");
        }

        String normalizedEmail = rawEmail.toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Email ou mot de passe incorrect"));

        if (!passwordService.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }

        Instant now = Instant.now();
        userRepository.updateLastActiveAt(user.getId(), now);
        user.setLastActiveAt(now);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserResponse.fromUser(user));
    }

    public void changePassword(ChangePasswordRequest request) {
        if (request == null) throw new ValidationException("Request body is required");

        String rawEmail = request.getEmail() != null ? request.getEmail().trim() : "";
        String currentPassword = request.getCurrentPassword() != null ? request.getCurrentPassword().trim() : "";
        String newPassword = request.getNewPassword() != null ? request.getNewPassword().trim() : "";

        if (rawEmail.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
            throw new ValidationException("Tous les champs sont obligatoires");
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Le nouveau mot de passe doit contenir au moins 8 caractères");
        }

        String normalizedEmail = rawEmail.toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé ou mot de passe actuel incorrect"));

        if (!passwordService.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Mot de passe actuel incorrect");
        }

        String newPasswordHash = passwordService.hashPassword(newPassword);
        userRepository.updatePassword(user.getId(), newPasswordHash);
    }

    public UserResponse getCurrentUser(UUID userId) {
        if (userId == null) throw new ValidationException("User ID invalid");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Utilisateur non trouvé"));
        return UserResponse.fromUser(user);
    }
}

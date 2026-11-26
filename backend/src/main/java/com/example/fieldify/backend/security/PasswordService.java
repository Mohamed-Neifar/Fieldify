package com.example.fieldify.backend.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {
    private static final int BCRYPT_LOG_ROUNDS = 12;

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}

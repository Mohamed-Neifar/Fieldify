package com.example.fieldify.backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String jwtSecret;
    private final int jwtExpirationMinutes;
    private final int dbPoolMaxSize;
    private final int serverPort;

    public AppConfig() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        this.dbUrl = getEnvOrDefault(dotenv, "DB_URL", "jdbc:postgresql://localhost:5432/Sportity");
        this.dbUser = getEnvOrDefault(dotenv, "DB_USER", "postgres");
        this.dbPassword = getEnvOrDefault(dotenv, "DB_PASSWORD", "12345");
        this.jwtSecret = getEnv(dotenv, "JWT_SECRET");
        this.jwtExpirationMinutes = Integer.parseInt(getEnvOrDefault(dotenv, "JWT_EXPIRATION_MINUTES", "60"));
        this.dbPoolMaxSize = Integer.parseInt(getEnvOrDefault(dotenv, "DB_POOL_MAX_SIZE", "10"));
        this.serverPort = Integer.parseInt(getEnvOrDefault(dotenv, "SERVER_PORT", "8081"));

        validate();
    }

    private String getEnv(Dotenv dotenv, String key) {
        String val = dotenv.get(key);
        if (val == null || val.trim().isEmpty()) {
            val = System.getenv(key);
        }
        return val;
    }

    private String getEnvOrDefault(Dotenv dotenv, String key, String defaultValue) {
        String val = getEnv(dotenv, key);
        return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
    }

    private void validate() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty() || jwtSecret.equals("GENERATE_A_NEW_RANDOM_SECRET")) {
            throw new IllegalStateException("JWT_SECRET environment variable is missing or insecure!");
        }
    }

    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public String getJwtSecret() { return jwtSecret; }
    public int getJwtExpirationMinutes() { return jwtExpirationMinutes; }
    public int getDbPoolMaxSize() { return dbPoolMaxSize; }
    public int getServerPort() { return serverPort; }
}

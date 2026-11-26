package com.example.fieldify.backend.repository;

import com.example.fieldify.backend.model.Role;
import com.example.fieldify.backend.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (id, full_name, email, password_hash, phone, role, last_active_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        Instant now = Instant.now();
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(now);
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, user.getId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole().name());
            ps.setTimestamp(7, user.getLastActiveAt() != null ? Timestamp.from(user.getLastActiveAt()) : null);
            ps.setTimestamp(8, Timestamp.from(user.getUpdatedAt()));

            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving user: " + e.getMessage(), e);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, full_name, email, password_hash, phone, role, last_active_at, updated_at " +
                     "FROM users WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(UUID id) {
        String sql = "SELECT id, full_name, email, password_hash, phone, role, last_active_at, updated_at " +
                     "FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking email existence: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean updatePassword(UUID id, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ps.setObject(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating password: " + e.getMessage(), e);
        }
    }

    public void updateLastActiveAt(UUID id, Instant lastActiveAt) {
        String sql = "UPDATE users SET last_active_at = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.from(lastActiveAt));
            ps.setObject(2, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating last active timestamp: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String phone = rs.getString("phone");
        Role role = Role.valueOf(rs.getString("role"));

        Timestamp lastActiveTs = rs.getTimestamp("last_active_at");
        Instant lastActiveAt = lastActiveTs != null ? lastActiveTs.toInstant() : null;

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        Instant updatedAt = updatedTs != null ? updatedTs.toInstant() : Instant.now();

        return new User(id, fullName, email, passwordHash, phone, role, lastActiveAt, updatedAt);
    }
}

package com.example.fieldify.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {

    private static final String PREF_NAME = "fieldify_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUserSession(long id, String name, String email, String role) {
        saveUserSession(id, name, email, "", role, null);
    }

    public void saveUserSession(long id, String name, String email, String phone, String role) {
        saveUserSession(id, name, email, phone, role, null);
    }

    public void saveUserSession(long id, String name, String email, String phone, String role, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name != null ? name : "");
        editor.putString(KEY_USER_EMAIL, email != null ? email : "");
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "");
        editor.putString(KEY_USER_ROLE, role != null ? role : "JOUEUR");
        if (token != null && !token.trim().isEmpty()) {
            editor.putString(KEY_JWT_TOKEN, token);
        }
        editor.apply();
    }

    public void saveToken(String token) {
        if (token != null) {
            editor.putString(KEY_JWT_TOKEN, token);
            editor.apply();
        }
    }

    public String getToken() {
        return pref.getString(KEY_JWT_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return pref.getLong(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "JOUEUR");
    }

    public Set<String> getFavoriteTerrainIds() {
        String email = getUserEmail();
        String key = "favorites_" + (email != null && !email.trim().isEmpty() ? email.toLowerCase().trim() : "guest");
        return pref.getStringSet(key, new HashSet<>());
    }

    public boolean isTerrainFavorite(String terrainId) {
        if (terrainId == null) return false;
        return getFavoriteTerrainIds().contains(terrainId);
    }

    public boolean isFavoriteTerrain(String terrainId) {
        return isTerrainFavorite(terrainId);
    }

    public void toggleFavoriteTerrainId(String terrainId) {
        if (terrainId == null) return;
        String email = getUserEmail();
        String key = "favorites_" + (email != null && !email.trim().isEmpty() ? email.toLowerCase().trim() : "guest");
        Set<String> set = new HashSet<>(getFavoriteTerrainIds());
        if (set.contains(terrainId)) {
            set.remove(terrainId);
        } else {
            set.add(terrainId);
        }
        editor.putStringSet(key, set);
        editor.apply();
    }

    public void toggleFavoriteTerrain(String terrainId) {
        toggleFavoriteTerrainId(terrainId);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

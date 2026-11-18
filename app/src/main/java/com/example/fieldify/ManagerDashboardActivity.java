package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import com.example.fieldify.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_manager_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(ManagerDashboardActivity.this, LoginActivity.class));
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.manager_bottom_navigation);
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new ManagerRequestsFragment());
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_manager_requests) {
                selectedFragment = new ManagerRequestsFragment();
            } else if (itemId == R.id.nav_manager_terrains) {
                selectedFragment = new ManagerTerrainsFragment();
            } else if (itemId == R.id.nav_manager_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_fragment_container, fragment)
                .commit();
    }
}
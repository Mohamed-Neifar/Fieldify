package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fieldify.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initUI();
    }

    private void initUI() {
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvUserEmail = findViewById(R.id.tv_user_email);
        TextView tvUserRole = findViewById(R.id.tv_user_role);
        Button btnLogout = findViewById(R.id.btn_logout);

        if (tvUserName != null) {
            tvUserName.setText(sessionManager.getUserName());
        }
        if (tvUserEmail != null) {
            tvUserEmail.setText(sessionManager.getUserEmail());
        }
        if (tvUserRole != null) {
            tvUserRole.setText(sessionManager.getUserRole());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.logout();
                Toast.makeText(HomeActivity.this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            });
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
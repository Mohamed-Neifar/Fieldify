package com.example.fieldify;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.AuthResponse;
import com.example.fieldify.network.model.LoginRequest;
import com.example.fieldify.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivPasswordToggle;
    private TextView tvForgotPassword;
    private Button btnLoginSubmit;
    private TextView tvRegisterPrompt;
    private View cardLogin;
    private View viewHeaderBg;
    private TextView tvLoginTitle;
    private SessionManager sessionManager;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        // Auto-login role check
        if (sessionManager.isLoggedIn()) {
            redirectBasedOnRole(sessionManager.getUserRole());
            return;
        }

        initViews();
        setupFormattedText();
        setupListeners();
        startEntranceAnimations();
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if ("MANAGER".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void initViews() {
        viewHeaderBg = findViewById(R.id.view_header_bg);
        tvLoginTitle = findViewById(R.id.tv_login_title);
        cardLogin = findViewById(R.id.card_login);
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        btnLoginSubmit = findViewById(R.id.btn_login_submit);
        tvRegisterPrompt = findViewById(R.id.tv_register_prompt);
    }

    private void setupFormattedText() {
        String prefix = getString(R.string.no_account_prefix);
        String action = getString(R.string.no_account_action);
        SpannableStringBuilder builder = new SpannableStringBuilder(prefix + action);
        int orangeColor = getColor(R.color.orange_primary);

        builder.setSpan(new ForegroundColorSpan(orangeColor), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegisterPrompt.setText(builder);
    }

    private void setupListeners() {
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());

        tvForgotPassword.setOnClickListener(v ->
            Toast.makeText(LoginActivity.this, R.string.toast_forgot_password, Toast.LENGTH_SHORT).show()
        );

        btnLoginSubmit.setOnClickListener(v -> attemptLogin());

        tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            applyActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_visible);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, R.string.error_password_length, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        LoginRequest loginRequest = new LoginRequest(email, password);
        RetrofitClient.getAuthApiService().login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    long userId = auth.getId() != null ? auth.getId() : 1L;
                    String name = !TextUtils.isEmpty(auth.getName()) ? auth.getName() : "Utilisateur";
                    String userEmail = !TextUtils.isEmpty(auth.getEmail()) ? auth.getEmail() : email;
                    String role = !TextUtils.isEmpty(auth.getRole()) ? auth.getRole() : "JOUEUR";

                    sessionManager.saveUserSession(userId, name, userEmail, "", role, auth.getToken());
                    Toast.makeText(LoginActivity.this, R.string.toast_login_success, Toast.LENGTH_LONG).show();

                    redirectBasedOnRole(role);
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "Compte non vérifié. Veuillez saisir votre code OTP.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    startActivity(intent);
                    applyActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "E-mail ou mot de passe incorrect.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Erreur de connexion (" + response.code() + ").", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoadingState(false);
                Toast.makeText(LoginActivity.this, "Impossible de contacter le serveur : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        btnLoginSubmit.setEnabled(!isLoading);
        btnLoginSubmit.setAlpha(isLoading ? 0.6f : 1.0f);
        if (isLoading) {
            btnLoginSubmit.setText("Connexion en cours...");
        } else {
            btnLoginSubmit.setText(R.string.btn_login);
        }
    }

    private void startEntranceAnimations() {
        viewHeaderBg.setAlpha(0f);
        viewHeaderBg.setTranslationY(-40f);

        tvLoginTitle.setAlpha(0f);
        tvLoginTitle.setTranslationY(-30f);

        cardLogin.setAlpha(0f);
        cardLogin.setTranslationY(60f);

        tvRegisterPrompt.setAlpha(0f);

        viewHeaderBg.animate().alpha(1f).translationY(0f).setDuration(400).start();
        tvLoginTitle.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();
        cardLogin.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        tvRegisterPrompt.animate().alpha(1f).setDuration(400).setStartDelay(400).start();
    }

    @SuppressWarnings("deprecation")
    private void applyActivityTransition(int enterAnim, int exitAnim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim);
        } else {
            overridePendingTransition(enterAnim, exitAnim);
        }
    }
}
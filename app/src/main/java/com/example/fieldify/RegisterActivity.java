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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private ImageView ivPasswordToggle;
    private Button btnRegisterSubmit;
    private TextView tvLoginPrompt;
    private View cardRegister;
    private View viewHeaderBg;
    private View layoutHeaderText;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupFormattedText();
        setupListeners();
        startEntranceAnimations();
    }

    private void initViews() {
        viewHeaderBg = findViewById(R.id.view_register_header_bg);
        layoutHeaderText = findViewById(R.id.layout_register_header_text);
        cardRegister = findViewById(R.id.card_register);
        etFullName = findViewById(R.id.et_register_fullname);
        etEmail = findViewById(R.id.et_register_email);
        etPhone = findViewById(R.id.et_register_phone);
        etPassword = findViewById(R.id.et_register_password);
        ivPasswordToggle = findViewById(R.id.iv_reg_password_toggle);
        btnRegisterSubmit = findViewById(R.id.btn_register_submit);
        tvLoginPrompt = findViewById(R.id.tv_login_prompt);
    }

    private void setupFormattedText() {
        // Formatted bottom link: "Déjà un compte ? " + "Se connecter" (orange + bold)
        String prefix = getString(R.string.already_account_prefix);
        String action = getString(R.string.already_account_action);
        SpannableStringBuilder builder = new SpannableStringBuilder(prefix + action);
        int orangeColor = getColor(R.color.orange_primary);

        builder.setSpan(new ForegroundColorSpan(orangeColor), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLoginPrompt.setText(builder);
    }

    private void setupListeners() {
        // Toggle password visibility
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Submit registration click
        btnRegisterSubmit.setOnClickListener(v -> attemptRegister());

        // Navigate to Login Activity
        tvLoginPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            applyActivityTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void attemptRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
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

        Toast.makeText(this, R.string.toast_register_success, Toast.LENGTH_LONG).show();

        // After successful registration, navigate to LoginActivity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        applyActivityTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void startEntranceAnimations() {
        viewHeaderBg.setAlpha(0f);
        viewHeaderBg.setTranslationY(-40f);

        layoutHeaderText.setAlpha(0f);
        layoutHeaderText.setTranslationY(-30f);

        cardRegister.setAlpha(0f);
        cardRegister.setTranslationY(60f);

        tvLoginPrompt.setAlpha(0f);

        viewHeaderBg.animate().alpha(1f).translationY(0f).setDuration(400).start();
        layoutHeaderText.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();
        cardRegister.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        tvLoginPrompt.animate().alpha(1f).setDuration(400).setStartDelay(400).start();
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
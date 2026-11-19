package com.example.fieldify;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.fieldify.network.model.SendOtpRequest;
import com.example.fieldify.network.model.UserDto;
import com.example.fieldify.network.model.VerifyOtpRequest;
import com.example.fieldify.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private String userEmail = "";
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5;
    private Button btnVerifySubmit;
    private TextView tvCardSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_verify_email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_verify_email), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("USER_EMAIL")) {
            userEmail = getIntent().getStringExtra("USER_EMAIL");
        }

        initUI();
        setupOtpFocusNavigation();
    }

    private void initUI() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvCardSubtitle = findViewById(R.id.tv_card_subtitle);
        if (tvCardSubtitle != null && !TextUtils.isEmpty(userEmail)) {
            tvCardSubtitle.setText("Veuillez saisir le code de vérification à 5 chiffres envoyé à " + userEmail + " pour continuer.");
        }

        etOtp1 = findViewById(R.id.et_otp_1);
        etOtp2 = findViewById(R.id.et_otp_2);
        etOtp3 = findViewById(R.id.et_otp_3);
        etOtp4 = findViewById(R.id.et_otp_4);
        etOtp5 = findViewById(R.id.et_otp_5);

        btnVerifySubmit = findViewById(R.id.btn_verify_submit);
        if (btnVerifySubmit != null) {
            btnVerifySubmit.setOnClickListener(v -> attemptVerifyOtp());
        }

        TextView tvResendPrompt = findViewById(R.id.tv_resend_prompt);
        if (tvResendPrompt != null) {
            String prefix = getString(R.string.resend_prompt_prefix);
            String action = getString(R.string.resend_action);
            SpannableStringBuilder builder = new SpannableStringBuilder(prefix + action);
            int orangeColor = getColor(R.color.orange_primary);

            builder.setSpan(new ForegroundColorSpan(orangeColor), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvResendPrompt.setText(builder);

            tvResendPrompt.setOnClickListener(v -> attemptResendOtp());
        }

        ImageView ivSandglass = findViewById(R.id.iv_sandglass);
        if (ivSandglass != null) {
            Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.anim_sandglass_rotate);
            ivSandglass.startAnimation(rotateAnim);
        }
    }

    private void setupOtpFocusNavigation() {
        EditText[] otpBoxes = new EditText[]{etOtp1, etOtp2, etOtp3, etOtp4, etOtp5};

        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpBoxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (otpBoxes[index].getText().length() == 0 && index > 0) {
                        otpBoxes[index - 1].requestFocus();
                        otpBoxes[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private void attemptVerifyOtp() {
        String code = getOtpCode();

        if (code.length() < 5) {
            Toast.makeText(this, "Veuillez saisir les 5 chiffres du code OTP.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Adresse e-mail manquante.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        VerifyOtpRequest request = new VerifyOtpRequest(userEmail, code);
        RetrofitClient.getAuthApiService().verifyOtp(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    SessionManager sessionManager = new SessionManager(VerifyEmailActivity.this);

                    UserDto user = auth.getUser();
                    if (user != null && user.getId() != null) {
                        sessionManager.saveUserSession(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhone(),
                                user.getRole()
                        );
                    } else if (auth.getId() != null) {
                        sessionManager.saveUserSession(
                                auth.getId(),
                                auth.getName(),
                                auth.getEmail(),
                                "",
                                auth.getRole()
                        );
                    }

                    Toast.makeText(VerifyEmailActivity.this, "Compte vérifié avec succès !", Toast.LENGTH_LONG).show();

                    String role = sessionManager.getUserRole();
                    Intent intent;
                    if ("MANAGER".equalsIgnoreCase(role) || "GESTIONNAIRE".equalsIgnoreCase(role)) {
                        intent = new Intent(VerifyEmailActivity.this, ManagerDashboardActivity.class);
                    } else {
                        intent = new Intent(VerifyEmailActivity.this, HomeActivity.class);
                    }
                    startActivity(intent);
                    applyActivityTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Code OTP invalide ou expiré.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoadingState(false);
                Toast.makeText(VerifyEmailActivity.this, "Erreur réseau : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptResendOtp() {
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Adresse e-mail non valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Renvoi du code en cours...", Toast.LENGTH_SHORT).show();

        SendOtpRequest request = new SendOtpRequest(userEmail);
        RetrofitClient.getAuthApiService().sendOtp(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Un nouveau code OTP a été envoyé à " + userEmail, Toast.LENGTH_LONG).show();
                } else {
                    resendOtpFallback();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                resendOtpFallback();
            }
        });
    }

    private void resendOtpFallback() {
        RetrofitClient.getAuthApiService().sendOtpQuery(userEmail).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Un nouveau code OTP a été envoyé à " + userEmail, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Échec du renvoi du code OTP.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, "Échec de connexion au serveur.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOtpCode() {
        StringBuilder sb = new StringBuilder();
        if (etOtp1 != null) sb.append(etOtp1.getText().toString().trim());
        if (etOtp2 != null) sb.append(etOtp2.getText().toString().trim());
        if (etOtp3 != null) sb.append(etOtp3.getText().toString().trim());
        if (etOtp4 != null) sb.append(etOtp4.getText().toString().trim());
        if (etOtp5 != null) sb.append(etOtp5.getText().toString().trim());
        return sb.toString();
    }

    private void setLoadingState(boolean isLoading) {
        if (btnVerifySubmit != null) {
            btnVerifySubmit.setEnabled(!isLoading);
            btnVerifySubmit.setAlpha(isLoading ? 0.6f : 1.0f);
            btnVerifySubmit.setText(isLoading ? "Vérification..." : getString(R.string.btn_verify));
        }
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
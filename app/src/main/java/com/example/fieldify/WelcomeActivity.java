package com.example.fieldify;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView imgIllustration;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private Button btnGetStarted;
    private TextView tvAlreadyHaveAccount;
    private View layoutLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_welcome), (v, insets) -> {
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
        layoutLogo = findViewById(R.id.layout_logo);
        imgIllustration = findViewById(R.id.img_sports_illustration);
        tvTitle = findViewById(R.id.tv_welcome_title);
        tvSubtitle = findViewById(R.id.tv_welcome_subtitle);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);
    }

    private void setupFormattedText() {
        // Formatted title: "Réservez votre\n" (white) + "terrain en un clic !" (orange)
        String part1 = getString(R.string.welcome_title_part1);
        String part2 = getString(R.string.welcome_title_part2);
        SpannableStringBuilder titleBuilder = new SpannableStringBuilder(part1 + part2);
        int orangeColor = getColor(R.color.orange_primary);
        titleBuilder.setSpan(new ForegroundColorSpan(orangeColor), part1.length(), (part1 + part2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(titleBuilder);

        // Formatted bottom link: "J'ai déjà un compte • " (muted) + "Se connecter" (orange + bold)
        String prefix = getString(R.string.welcome_already_account_prefix);
        String action = getString(R.string.welcome_already_account_action);
        SpannableStringBuilder accountBuilder = new SpannableStringBuilder(prefix + action);
        accountBuilder.setSpan(new ForegroundColorSpan(orangeColor), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        accountBuilder.setSpan(new StyleSpan(Typeface.BOLD), prefix.length(), (prefix + action).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAlreadyHaveAccount.setText(accountBuilder);
    }

    private void setupListeners() {
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
            applyActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            applyActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void startEntranceAnimations() {
        layoutLogo.setAlpha(0f);
        layoutLogo.setTranslationY(-30f);

        imgIllustration.setAlpha(0f);
        imgIllustration.setScaleX(0.9f);
        imgIllustration.setScaleY(0.9f);
        imgIllustration.setTranslationY(40f);

        tvTitle.setAlpha(0f);
        tvTitle.setTranslationY(40f);

        tvSubtitle.setAlpha(0f);
        tvSubtitle.setTranslationY(40f);

        btnGetStarted.setAlpha(0f);
        btnGetStarted.setTranslationY(50f);

        tvAlreadyHaveAccount.setAlpha(0f);

        layoutLogo.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();
        imgIllustration.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(0f).setDuration(600).setStartDelay(250).start();
        tvTitle.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(400).start();
        tvSubtitle.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(500).start();
        btnGetStarted.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(600).start();
        tvAlreadyHaveAccount.animate().alpha(1f).setDuration(500).setStartDelay(700).start();
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
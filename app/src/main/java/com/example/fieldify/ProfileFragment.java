package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fieldify.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etCity;
    private Button btnSave;
    private Button btnLogout;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        loadSessionData();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_profile_name);
        etPhone = view.findViewById(R.id.et_profile_phone);
        etEmail = view.findViewById(R.id.et_profile_email);
        etCity = view.findViewById(R.id.et_profile_city);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnLogout = view.findViewById(R.id.btn_profile_logout);
    }

    private void loadSessionData() {
        if (etName != null) {
            etName.setText(sessionManager.getUserName());
        }
        if (etEmail != null) {
            etEmail.setText(sessionManager.getUserEmail());
        }
        if (etPhone != null) {
            etPhone.setText("+216 22 123 456");
        }
        if (etCity != null) {
            etCity.setText("Sfax, Tunisie");
        }
    }

    private void setupListeners() {
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String newName = etName.getText() != null ? etName.getText().toString().trim() : "";
                if (!newName.isEmpty()) {
                    sessionManager.saveUserSession(
                            sessionManager.getUserId(),
                            newName,
                            sessionManager.getUserEmail(),
                            sessionManager.getUserRole()
                    );
                    Toast.makeText(requireContext(), "Modifications enregistrées avec succès !", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.logout();
                Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }
}
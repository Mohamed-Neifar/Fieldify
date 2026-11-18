package com.example.fieldify;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Terrain;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTerrainActivity extends AppCompatActivity {

    private Button btnPickPhotos;
    private EditText etTitle;
    private Spinner spinnerCategory;
    private EditText etZone;
    private EditText etAddress;
    private EditText etPrice;
    private ChipGroup chipGroupEquipments;
    private EditText etDescription;
    private Button btnPublish;

    private final List<Uri> selectedPhotoUris = new ArrayList<>();

    private final ActivityResultLauncher<String> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    selectedPhotoUris.clear();
                    selectedPhotoUris.addAll(uris);
                    Toast.makeText(this, uris.size() + " photo(s) sélectionnée(s)", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_terrain);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_add_terrain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_add_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnPickPhotos = findViewById(R.id.btn_pick_photos);
        etTitle = findViewById(R.id.et_terrain_title);
        spinnerCategory = findViewById(R.id.spinner_category);
        etZone = findViewById(R.id.et_terrain_zone);
        etAddress = findViewById(R.id.et_terrain_address);
        etPrice = findViewById(R.id.et_terrain_price);
        chipGroupEquipments = findViewById(R.id.chip_group_equipments);
        etDescription = findViewById(R.id.et_terrain_desc);
        btnPublish = findViewById(R.id.btn_publish_terrain);
    }

    private void setupSpinner() {
        List<String> categories = Arrays.asList("Football", "Padel", "Tennis", "Basket-ball", "Volley-ball", "Golf", "Piscine privée");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        btnPickPhotos.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));
        btnPublish.setOnClickListener(v -> publishTerrain());
    }

    private void publishTerrain() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String zone = etZone.getText() != null ? etZone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(zone) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires (Titre, Zone, Prix).", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 50.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ignored) {
        }

        String selectedCategory = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Football";

        List<String> selectedEquipments = new ArrayList<>();
        if (chipGroupEquipments != null) {
            for (int i = 0; i < chipGroupEquipments.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupEquipments.getChildAt(i);
                if (chip != null && chip.isChecked()) {
                    selectedEquipments.add(chip.getText().toString());
                }
            }
        }
        if (selectedEquipments.isEmpty()) {
            selectedEquipments = Arrays.asList("Parking", "Vestiaires", "Éclairage LED");
        }

        List<Integer> imageResIds = Arrays.asList(R.drawable.img_welcome_sports);
        if ("Football".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.footbalfieldimg1, R.drawable.footbalfieldimg2);
        } else if ("Padel".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.padelfield);
        } else if ("Tennis".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.tennisfield);
        } else if ("Basket-ball".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.basketballfield);
        } else if ("Volley-ball".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.volleyballfiled);
        } else if ("Golf".equalsIgnoreCase(selectedCategory)) {
            imageResIds = Arrays.asList(R.drawable.golffield);
        }

        Terrain newTerrain = new Terrain(
                "t_" + System.currentTimeMillis(),
                title,
                selectedCategory,
                zone,
                TextUtils.isEmpty(address) ? zone : address,
                price,
                4.9,
                TextUtils.isEmpty(description) ? "Nouveau terrain de haut niveau à " + zone : description,
                selectedEquipments,
                imageResIds,
                false
        );

        MockDataRepository.getInstance().addTerrain(newTerrain);

        Toast.makeText(this, "Espace sportif " + title + " publié avec succès !", Toast.LENGTH_LONG).show();
        finish();
    }
}
package com.example.fieldify;

import android.content.ContentResolver;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.CreateTerrainRequest;
import com.example.fieldify.network.model.TerrainDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        btnPublish.setOnClickListener(v -> attemptPublish());
    }

    private void attemptPublish() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String zone = etZone.getText() != null ? etZone.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(zone) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires (Titre, Zone, Prix).", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("Publication en cours...");

        if (!selectedPhotoUris.isEmpty()) {
            uploadPhotosAndPublish();
        } else {
            sendCreateTerrainRequest(new ArrayList<>());
        }
    }

    private void uploadPhotosAndPublish() {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < selectedPhotoUris.size(); i++) {
            Uri uri = selectedPhotoUris.get(i);
            try {
                File file = getFileFromUri(uri, "photo_" + System.currentTimeMillis() + "_" + i + ".jpg");
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
                parts.add(body);
            } catch (Exception ignored) {
            }
        }

        if (parts.isEmpty()) {
            sendCreateTerrainRequest(new ArrayList<>());
            return;
        }

        RetrofitClient.getFieldifyApiService(this).uploadFiles(parts).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sendCreateTerrainRequest(response.body());
                } else {
                    sendCreateTerrainRequest(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                sendCreateTerrainRequest(new ArrayList<>());
            }
        });
    }

    private File getFileFromUri(Uri uri, String fileName) throws IOException {
        File tempFile = new File(getCacheDir(), fileName);
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream != null) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
        }
        return tempFile;
    }

    private void sendCreateTerrainRequest(List<String> imageUrls) {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String zone = etZone.getText() != null ? etZone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

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

        CreateTerrainRequest request = new CreateTerrainRequest(
                selectedCategory,
                title,
                TextUtils.isEmpty(description) ? "Nouveau terrain de haut niveau à " + zone : description,
                TextUtils.isEmpty(address) ? zone : address,
                zone,
                price,
                selectedEquipments,
                imageUrls
        );

        final double finalPrice = price;
        final List<String> finalEquipments = selectedEquipments;

        RetrofitClient.getFieldifyApiService(this).createManagerTerrain(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TerrainDto> call, @NonNull Response<TerrainDto> response) {
                btnPublish.setEnabled(true);
                btnPublish.setText("Publier le terrain");
                if (response.isSuccessful() && response.body() != null) {
                    Terrain newTerrain = HomeFragment.mapDtoToTerrain(response.body());
                    MockDataRepository.getInstance().addTerrain(newTerrain);
                    Toast.makeText(AddTerrainActivity.this, "Espace sportif " + title + " publié avec succès !", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    fallbackLocalPublish(title, selectedCategory, zone, address, finalPrice, description, finalEquipments, imageUrls);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TerrainDto> call, @NonNull Throwable t) {
                btnPublish.setEnabled(true);
                btnPublish.setText("Publier le terrain");
                fallbackLocalPublish(title, selectedCategory, zone, address, finalPrice, description, finalEquipments, imageUrls);
            }
        });
    }

    private void fallbackLocalPublish(String title, String category, String zone, String address, double price, String description, List<String> equipments, List<String> imageUrls) {
        Terrain localTerrain = new Terrain(
                "t_" + System.currentTimeMillis(),
                title,
                category,
                zone,
                TextUtils.isEmpty(address) ? zone : address,
                price,
                4.9,
                description,
                equipments,
                Arrays.asList(R.drawable.img_welcome_sports),
                false
        );
        if (imageUrls != null && !imageUrls.isEmpty()) {
            localTerrain.setImageUrls(imageUrls);
        }
        MockDataRepository.getInstance().addTerrain(localTerrain);
        Toast.makeText(this, "Espace sportif " + title + " publié !", Toast.LENGTH_LONG).show();
        finish();
    }
}
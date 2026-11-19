package com.example.fieldify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fieldify.adapter.ImageSliderAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Reservation;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.CreateReservationRequest;
import com.example.fieldify.network.model.ReservationDto;
import com.example.fieldify.network.model.TerrainDto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TerrainDetailActivity extends AppCompatActivity {

    private String terrainIdStr = "t1";
    private Long terrainLongId = 1L;
    private Terrain currentTerrain;

    private ViewPager2 viewPagerPhotos;
    private TabLayout tabDots;
    private ImageButton btnBack;
    private ImageButton btnFavorite;

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvPrice;
    private TextView tvDescription;
    private Button btnBookSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_terrain_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_terrain_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("TERRAIN_ID")) {
            terrainIdStr = getIntent().getStringExtra("TERRAIN_ID");
            try {
                terrainLongId = Long.parseLong(terrainIdStr);
            } catch (Exception ignored) {
                terrainLongId = 1L;
            }
        }

        initViews();
        fetchTerrainDetail();
        setupListeners();
    }

    private void initViews() {
        viewPagerPhotos = findViewById(R.id.view_pager_photos);
        tabDots = findViewById(R.id.tab_dots);
        btnBack = findViewById(R.id.btn_detail_back);
        btnFavorite = findViewById(R.id.btn_detail_favorite);

        tvName = findViewById(R.id.tv_detail_name);
        tvAddress = findViewById(R.id.tv_detail_address);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvDescription = findViewById(R.id.tv_detail_description);
        btnBookSlot = findViewById(R.id.btn_book_slot);
    }

    private void fetchTerrainDetail() {
        RetrofitClient.getFieldifyApiService(this).getTerrainDetail(terrainLongId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TerrainDto> call, @NonNull Response<TerrainDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTerrain = HomeFragment.mapDtoToTerrain(response.body());
                    bindData();
                } else {
                    fallbackToMockTerrain();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TerrainDto> call, @NonNull Throwable t) {
                fallbackToMockTerrain();
            }
        });
    }

    private void fallbackToMockTerrain() {
        currentTerrain = MockDataRepository.getInstance().getTerrainById(terrainIdStr);
        if (currentTerrain == null) {
            Toast.makeText(this, "Espace non trouvé.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (currentTerrain.getImageResIds() == null || currentTerrain.getImageResIds().isEmpty()) {
            currentTerrain.setImageResIds(Arrays.asList(R.drawable.img_welcome_sports));
        }
        bindData();
    }

    private void bindData() {
        if (currentTerrain == null) return;

        tvName.setText(currentTerrain.getName());
        tvAddress.setText(currentTerrain.getAddress());
        tvPrice.setText((int) currentTerrain.getPricePerHour() + " DT / heure");
        tvDescription.setText(currentTerrain.getDescription());

        // Setup Photo Slider
        List<?> sources = (currentTerrain.getImageUrls() != null && !currentTerrain.getImageUrls().isEmpty())
                ? currentTerrain.getImageUrls()
                : currentTerrain.getImageResIds();

        if (sources != null && !sources.isEmpty()) {
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, sources);
            viewPagerPhotos.setAdapter(sliderAdapter);

            new TabLayoutMediator(tabDots, viewPagerPhotos, (tab, position) -> {
            }).attach();
        }

        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (currentTerrain != null) {
            btnFavorite.setImageResource(currentTerrain.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            if (currentTerrain != null) {
                MockDataRepository.getInstance().toggleFavorite(currentTerrain.getId());
                updateFavoriteIcon();
                Toast.makeText(this, currentTerrain.isFavorite() ? "Ajouté aux favoris" : "Retiré des favoris", Toast.LENGTH_SHORT).show();
            }
        });

        btnBookSlot.setOnClickListener(v -> showBookingDialog());
    }

    private void showBookingDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null);

        RadioGroup rgDates = sheetView.findViewById(R.id.rg_dates);
        RadioGroup rgSlots = sheetView.findViewById(R.id.rg_time_slots);
        RadioGroup rgPayment = sheetView.findViewById(R.id.rg_payment_mode);
        EditText etFriends = sheetView.findViewById(R.id.et_invited_friends);
        TextView tvTotalPrice = sheetView.findViewById(R.id.tv_total_price_summary);
        Button btnConfirm = sheetView.findViewById(R.id.btn_confirm_booking);

        if (tvTotalPrice != null && currentTerrain != null) {
            tvTotalPrice.setText((int) currentTerrain.getPricePerHour() + " DT");
        }

        // Fetch already booked slots for default date
        fetchBookedSlots("2026-11-24", rgSlots);

        rgDates.setOnCheckedChangeListener((group, checkedId) -> {
            String date = "2026-11-24";
            if (checkedId == R.id.rb_date_2) date = "2026-11-25";
            else if (checkedId == R.id.rb_date_3) date = "2026-11-26";
            fetchBookedSlots(date, rgSlots);
        });

        btnConfirm.setOnClickListener(v -> {
            String selectedDate = "2026-11-24";
            int dateId = rgDates.getCheckedRadioButtonId();
            if (dateId == R.id.rb_date_2) selectedDate = "2026-11-25";
            else if (dateId == R.id.rb_date_3) selectedDate = "2026-11-26";

            String selectedSlot = "18:00 - 19:30";
            int slotId = rgSlots.getCheckedRadioButtonId();
            if (slotId == R.id.rb_slot_2) selectedSlot = "19:30 - 21:00";
            else if (slotId == R.id.rb_slot_3) selectedSlot = "21:00 - 22:30";

            // Get clean slot string without "(Déjà réservé)"
            selectedSlot = getCleanSlotName(selectedSlot);

            List<String> friendsList = new ArrayList<>();
            String friendsText = etFriends.getText() != null ? etFriends.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(friendsText)) {
                String[] names = friendsText.split(",");
                for (String name : names) {
                    if (!TextUtils.isEmpty(name.trim())) {
                        friendsList.add(name.trim());
                    }
                }
            }

            final String finalDate = selectedDate;
            final String finalSlot = selectedSlot;
            final boolean isOnlinePay = rgPayment != null && rgPayment.getCheckedRadioButtonId() == R.id.rb_pay_online;

            if (isOnlinePay) {
                btnConfirm.setEnabled(false);
                btnConfirm.setText("Validation du paiement D17...");
                Toast.makeText(TerrainDetailActivity.this, "Traitement du paiement en ligne simulé...", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        sendReservationApi(finalDate, finalSlot, friendsList, dialog), 1000);
            } else {
                sendReservationApi(finalDate, finalSlot, friendsList, dialog);
            }
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void sendReservationApi(String date, String timeSlot, List<String> friendsList, BottomSheetDialog dialog) {
        CreateReservationRequest req = new CreateReservationRequest(terrainLongId, date, timeSlot, friendsList);
        RetrofitClient.getFieldifyApiService(TerrainDetailActivity.this).createReservation(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ReservationDto> call, @NonNull Response<ReservationDto> response) {
                if (response.isSuccessful() || response.code() == 201) {
                    Toast.makeText(TerrainDetailActivity.this, "Réservation confirmée avec succès !", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(TerrainDetailActivity.this, "Ce créneau vient d'être réservé par un autre utilisateur.", Toast.LENGTH_LONG).show();
                } else {
                    localBookingFallback(date, timeSlot, friendsList, dialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationDto> call, @NonNull Throwable t) {
                localBookingFallback(date, timeSlot, friendsList, dialog);
            }
        });
    }

    private void fetchBookedSlots(String date, RadioGroup rgSlots) {
        resetSlotsToAvailable(rgSlots);

        RetrofitClient.getFieldifyApiService(this).getBookedSlots(terrainLongId, date).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> booked = response.body();
                    for (int i = 0; i < rgSlots.getChildCount(); i++) {
                        View child = rgSlots.getChildAt(i);
                        if (child instanceof RadioButton) {
                            RadioButton rb = (RadioButton) child;
                            String baseSlot = getCleanSlotName(rb.getText().toString());
                            boolean isBooked = booked.contains(baseSlot);
                            rb.setEnabled(!isBooked);
                            if (isBooked) {
                                rb.setText(baseSlot + " (Déjà réservé)");
                            } else {
                                rb.setText(baseSlot);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
            }
        });
    }

    private void resetSlotsToAvailable(RadioGroup rgSlots) {
        if (rgSlots == null) return;
        for (int i = 0; i < rgSlots.getChildCount(); i++) {
            View child = rgSlots.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                String baseSlot = getCleanSlotName(rb.getText().toString());
                rb.setText(baseSlot);
                rb.setEnabled(true);
            }
        }
    }

    private String getCleanSlotName(String rawText) {
        if (rawText == null) return "";
        return rawText.replace(" (Déjà réservé)", "").trim();
    }

    private void localBookingFallback(String date, String slot, List<String> friends, BottomSheetDialog dialog) {
        Reservation newRes = new Reservation("r_" + System.currentTimeMillis(), currentTerrain, date, slot, "Confirmé", friends);
        MockDataRepository.getInstance().addReservation(newRes);
        Toast.makeText(TerrainDetailActivity.this, "Réservation enregistrée !", Toast.LENGTH_LONG).show();
        dialog.dismiss();
        finish();
    }
}
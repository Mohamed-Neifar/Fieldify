package com.example.fieldify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.CreateReservationRequest;
import com.example.fieldify.network.model.ReservationDto;
import com.example.fieldify.network.model.TerrainDto;
import com.example.fieldify.network.model.UserDto;
import com.example.fieldify.utils.SessionManager;
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

    private String terrainIdStr = "1";
    private Long terrainLongId = 1L;
    private Terrain currentTerrain;
    private SessionManager sessionManager;

    private ViewPager2 viewPagerPhotos;
    private TabLayout tabDots;
    private ImageButton btnBack;
    private ImageButton btnFavorite;

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvPrice;
    private TextView tvDescription;
    private Button btnBookSlot;

    // Equipment View Containers
    private View eqParking, eqVestiaires, eqLight, eqCafe, eqShower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_terrain_detail);

        sessionManager = new SessionManager(this);

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

        eqParking = findViewById(R.id.eq_parking);
        eqVestiaires = findViewById(R.id.eq_vestiaires);
        eqLight = findViewById(R.id.eq_light);
        eqCafe = findViewById(R.id.eq_cafe);
        eqShower = findViewById(R.id.eq_shower);
    }

    private void fetchTerrainDetail() {
        RetrofitClient.getFieldifyApiService(this).getTerrainDetail(terrainLongId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TerrainDto> call, @NonNull Response<TerrainDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTerrain = HomeFragment.mapDtoToTerrain(response.body());
                    bindData();
                } else {
                    Toast.makeText(TerrainDetailActivity.this, "Impossible de charger les détails du terrain.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TerrainDto> call, @NonNull Throwable t) {
                Toast.makeText(TerrainDetailActivity.this, "Erreur réseau : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

        // Dynamic Equipment Display
        bindEquipments(currentTerrain.getEquipments());

        updateFavoriteIcon();
    }

    private void bindEquipments(List<String> equipments) {
        if (equipments == null) equipments = new ArrayList<>();
        List<String> lowerEquip = new ArrayList<>();
        for (String eq : equipments) {
            if (eq != null) lowerEquip.add(eq.toLowerCase().trim());
        }

        if (eqParking != null) eqParking.setVisibility(lowerEquip.contains("parking") ? View.VISIBLE : View.GONE);
        if (eqVestiaires != null) eqVestiaires.setVisibility(lowerEquip.contains("vestiaires") || lowerEquip.contains("vestiaire") ? View.VISIBLE : View.GONE);
        if (eqLight != null) eqLight.setVisibility(lowerEquip.contains("éclairage led") || lowerEquip.contains("eclairage") || lowerEquip.contains("lumière") ? View.VISIBLE : View.GONE);
        if (eqCafe != null) eqCafe.setVisibility(lowerEquip.contains("café") || lowerEquip.contains("cafe") || lowerEquip.contains("buvette") ? View.VISIBLE : View.GONE);
        if (eqShower != null) eqShower.setVisibility(lowerEquip.contains("douches") || lowerEquip.contains("douche") ? View.VISIBLE : View.GONE);
    }

    private void updateFavoriteIcon() {
        if (currentTerrain != null) {
            boolean isFav = sessionManager.isTerrainFavorite(currentTerrain.getId());
            currentTerrain.setFavorite(isFav);
            btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            if (currentTerrain != null) {
                sessionManager.toggleFavoriteTerrainId(currentTerrain.getId());
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
        AutoCompleteTextView etFriends = sheetView.findViewById(R.id.et_invited_friends);
        TextView tvTotalPrice = sheetView.findViewById(R.id.tv_total_price_summary);
        Button btnConfirm = sheetView.findViewById(R.id.btn_confirm_booking);

        if (tvTotalPrice != null && currentTerrain != null) {
            tvTotalPrice.setText((int) currentTerrain.getPricePerHour() + " DT");
        }

        // Fetch registered players for autocomplete dropdown
        fetchRegisteredPlayers(etFriends);

        // Fetch already booked slots for default date ("2026-11-24")
        final String[] activeDate = {"2026-11-24"};
        fetchBookedSlots(activeDate[0], rgSlots);

        rgDates.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_date_1) activeDate[0] = "2026-11-24";
            else if (checkedId == R.id.rb_date_2) activeDate[0] = "2026-11-25";
            else if (checkedId == R.id.rb_date_3) activeDate[0] = "2026-11-26";
            fetchBookedSlots(activeDate[0], rgSlots);
        });

        btnConfirm.setOnClickListener(v -> {
            String selectedSlot = "18:00 - 19:30";
            int slotId = rgSlots.getCheckedRadioButtonId();
            if (slotId == R.id.rb_slot_1) selectedSlot = "18:00 - 19:30";
            else if (slotId == R.id.rb_slot_2) selectedSlot = "19:30 - 21:00";
            else if (slotId == R.id.rb_slot_3) selectedSlot = "21:00 - 22:30";

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

            final boolean isOnlinePay = rgPayment != null && rgPayment.getCheckedRadioButtonId() == R.id.rb_pay_online;
            if (isOnlinePay) {
                Toast.makeText(TerrainDetailActivity.this, "Le paiement en ligne n'est pas encore disponible. Veuillez choisir le paiement sur place.", Toast.LENGTH_LONG).show();
                return;
            }

            // Prevent duplicate submissions
            btnConfirm.setEnabled(false);
            btnConfirm.setText("Réservation en cours...");

            sendReservationApi(activeDate[0], selectedSlot, friendsList, dialog, btnConfirm);
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void fetchRegisteredPlayers(AutoCompleteTextView actvFriends) {
        if (actvFriends == null) return;

        RetrofitClient.getFieldifyApiService(this).getRegisteredPlayers().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDto>> call, @NonNull Response<List<UserDto>> response) {
                List<String> playerItems = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (UserDto u : response.body()) {
                        if (!TextUtils.isEmpty(u.getEmail())) {
                            playerItems.add(u.getName() + " (" + u.getEmail() + ")");
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(TerrainDetailActivity.this, android.R.layout.simple_dropdown_item_1line, playerItems);
                actvFriends.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDto>> call, @NonNull Throwable t) {
            }
        });
    }

    private void sendReservationApi(String date, String timeSlot, List<String> friendsList, BottomSheetDialog dialog, Button btnConfirm) {
        CreateReservationRequest req = new CreateReservationRequest(terrainLongId, date, timeSlot, friendsList);
        RetrofitClient.getFieldifyApiService(TerrainDetailActivity.this).createReservation(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ReservationDto> call, @NonNull Response<ReservationDto> response) {
                if (response.isSuccessful() || response.code() == 201) {
                    Toast.makeText(TerrainDetailActivity.this, "Réservation confirmée avec succès !", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(TerrainDetailActivity.this, "Ce créneau vient d'être réservé par un autre utilisateur. Veuillez en choisir un autre.", Toast.LENGTH_LONG).show();
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("Confirmer la réservation");
                    fetchBookedSlots(date, (RadioGroup) dialog.findViewById(R.id.rg_time_slots));
                } else {
                    Toast.makeText(TerrainDetailActivity.this, "Erreur lors de la réservation (" + response.code() + ").", Toast.LENGTH_LONG).show();
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("Confirmer la réservation");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationDto> call, @NonNull Throwable t) {
                Toast.makeText(TerrainDetailActivity.this, "Échec de connexion au serveur : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Confirmer la réservation");
            }
        });
    }

    private void fetchBookedSlots(String date, RadioGroup rgSlots) {
        if (rgSlots == null) return;
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
}

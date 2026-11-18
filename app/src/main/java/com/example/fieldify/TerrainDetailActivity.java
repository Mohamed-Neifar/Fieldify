package com.example.fieldify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerrainDetailActivity extends AppCompatActivity {

    private String terrainId = "t1";
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
            terrainId = getIntent().getStringExtra("TERRAIN_ID");
        }

        currentTerrain = MockDataRepository.getInstance().getTerrainById(terrainId);

        if (currentTerrain == null) {
            Toast.makeText(this, "Espace sportif non trouvé.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ensure image list is never null or empty
        if (currentTerrain.getImageResIds() == null || currentTerrain.getImageResIds().isEmpty()) {
            currentTerrain.setImageResIds(Arrays.asList(R.drawable.img_welcome_sports));
        }

        initViews();
        bindData();
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

    private void bindData() {
        if (currentTerrain == null) return;

        tvName.setText(currentTerrain.getName());
        tvAddress.setText(currentTerrain.getAddress());
        tvPrice.setText((int) currentTerrain.getPricePerHour() + " DT / heure");
        tvDescription.setText(currentTerrain.getDescription());

        // Setup Photo Slider
        List<Integer> images = currentTerrain.getImageResIds();
        if (images != null && !images.isEmpty()) {
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, images);
            viewPagerPhotos.setAdapter(sliderAdapter);

            new TabLayoutMediator(tabDots, viewPagerPhotos, (tab, position) -> {
                // empty for dots
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
        EditText etFriends = sheetView.findViewById(R.id.et_invited_friends);
        Button btnConfirm = sheetView.findViewById(R.id.btn_confirm_booking);

        btnConfirm.setOnClickListener(v -> {
            String selectedDate = "2026-11-24";
            int dateId = rgDates.getCheckedRadioButtonId();
            if (dateId == R.id.rb_date_2) {
                selectedDate = "2026-11-25";
            } else if (dateId == R.id.rb_date_3) {
                selectedDate = "2026-11-26";
            }

            String selectedSlot = "18:00 - 19:30";
            int slotId = rgSlots.getCheckedRadioButtonId();
            if (slotId == R.id.rb_slot_2) {
                selectedSlot = "19:30 - 21:00";
            } else if (slotId == R.id.rb_slot_3) {
                selectedSlot = "21:00 - 22:30";
            }

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

            Reservation newRes = new Reservation(
                    "r_" + System.currentTimeMillis(),
                    currentTerrain,
                    selectedDate,
                    selectedSlot,
                    "Confirmé",
                    friendsList
            );

            MockDataRepository.getInstance().addReservation(newRes);

            Toast.makeText(TerrainDetailActivity.this, "Réservation confirmée ! Retrouvez-la dans Mes Matchs et votre Agenda.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            finish();
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }
}
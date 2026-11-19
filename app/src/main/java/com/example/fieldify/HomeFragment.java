package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.adapter.CategoryAdapter;
import com.example.fieldify.adapter.TerrainAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.SportDto;
import com.example.fieldify.network.model.TerrainDto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private RecyclerView rvTerrains;
    private EditText etSearch;
    private ImageButton btnFilter;
    private TextView tvEmpty;

    private TerrainAdapter terrainAdapter;

    private String selectedCategory = "Tous";
    private String searchQuery = "";
    private double maxPriceFilter = 200.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupCategories();
        setupTerrains();
        setupSearchAndFilter();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTerrainsList();
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvTerrains = view.findViewById(R.id.rv_terrains);
        etSearch = view.findViewById(R.id.et_search_query);
        btnFilter = view.findViewById(R.id.btn_open_filter);
        tvEmpty = view.findViewById(R.id.tv_empty_terrains);
    }

    private void setupCategories() {
        // Fetch real sports from API
        RetrofitClient.getFieldifyApiService(requireContext()).getSports().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SportDto>> call, @NonNull Response<List<SportDto>> response) {
                List<String> categories = new ArrayList<>();
                categories.add("Tous");
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (SportDto sport : response.body()) {
                        if (sport.getName() != null && !sport.getName().isEmpty()) {
                            categories.add(sport.getName());
                        }
                    }
                } else {
                    categories.addAll(Arrays.asList("Football", "Padel", "Tennis", "Basket-ball", "Volley-ball", "Golf", "Piscine privée"));
                }
                bindCategories(categories);
            }

            @Override
            public void onFailure(@NonNull Call<List<SportDto>> call, @NonNull Throwable t) {
                List<String> categories = Arrays.asList("Tous", "Football", "Padel", "Tennis", "Basket-ball", "Volley-ball", "Golf", "Piscine privée");
                bindCategories(categories);
            }
        });
    }

    private void bindCategories(List<String> categories) {
        if (!isAdded()) return;
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), categories, category -> {
            selectedCategory = category;
            refreshTerrainsList();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupTerrains() {
        List<Terrain> initialTerrains = MockDataRepository.getInstance().getTerrains();
        terrainAdapter = new TerrainAdapter(requireContext(), initialTerrains, new TerrainAdapter.OnTerrainClickListener() {
            @Override
            public void onTerrainClick(Terrain terrain) {
                Intent intent = new Intent(requireContext(), TerrainDetailActivity.class);
                intent.putExtra("TERRAIN_ID", terrain.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Terrain terrain) {
                MockDataRepository.getInstance().toggleFavorite(terrain.getId());
                refreshTerrainsList();
            }
        });

        rvTerrains.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTerrains.setAdapter(terrainAdapter);
    }

    private void setupSearchAndFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                refreshTerrainsList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void refreshTerrainsList() {
        String sportQuery = "Tous".equalsIgnoreCase(selectedCategory) ? null : selectedCategory;
        RetrofitClient.getFieldifyApiService(requireContext()).getTerrains(
                sportQuery, null, maxPriceFilter > 0 ? maxPriceFilter : null, searchQuery
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TerrainDto>> call, @NonNull Response<List<TerrainDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Terrain> apiTerrains = new ArrayList<>();
                    for (TerrainDto dto : response.body()) {
                        apiTerrains.add(mapDtoToTerrain(dto));
                    }
                    if (terrainAdapter != null) {
                        terrainAdapter.updateData(apiTerrains);
                    }
                    if (tvEmpty != null) {
                        tvEmpty.setVisibility(apiTerrains.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    fallbackToMockTerrains();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TerrainDto>> call, @NonNull Throwable t) {
                fallbackToMockTerrains();
            }
        });
    }

    private void fallbackToMockTerrains() {
        List<Terrain> filtered = MockDataRepository.getInstance().filterTerrains(selectedCategory, searchQuery, maxPriceFilter, null);
        if (terrainAdapter != null) {
            terrainAdapter.updateData(filtered);
        }
        if (tvEmpty != null) {
            tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public static Terrain mapDtoToTerrain(TerrainDto dto) {
        Terrain t = new Terrain();
        t.setId(dto.getId() != null ? String.valueOf(dto.getId()) : "t_" + System.currentTimeMillis());
        t.setName(dto.getName() != null ? dto.getName() : "Espace Sportif");
        t.setCategory(dto.getCategory());
        t.setZone(dto.getZone());
        t.setAddress(dto.getAddress() != null ? dto.getAddress() : dto.getZone());
        t.setPricePerHour(dto.getPricePerHour());
        t.setRating(dto.getRating());
        t.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        t.setEquipments(dto.getEquipments() != null ? dto.getEquipments() : new ArrayList<>());
        t.setImageUrls(dto.getImageUrls());
        return t;
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);

        TextView tvPriceVal = sheetView.findViewById(R.id.tv_filter_price_val);
        Slider sliderPrice = sheetView.findViewById(R.id.slider_max_price);
        View btnApply = sheetView.findViewById(R.id.btn_apply_filter);

        if (sliderPrice != null) {
            sliderPrice.setValue((float) maxPriceFilter);
            sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
                maxPriceFilter = value;
                if (tvPriceVal != null) {
                    tvPriceVal.setText((int) value + " DT / h");
                }
            });
        }

        if (btnApply != null) {
            btnApply.setOnClickListener(v -> {
                refreshTerrainsList();
                dialog.dismiss();
            });
        }

        dialog.setContentView(sheetView);
        dialog.show();
    }
}
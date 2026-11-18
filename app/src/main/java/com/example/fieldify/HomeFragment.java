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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private RecyclerView rvTerrains;
    private EditText etSearch;
    private ImageButton btnFilter;
    private TextView tvEmpty;

    private TerrainAdapter terrainAdapter;
    private CategoryAdapter categoryAdapter;

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
        List<String> categories = Arrays.asList("Tous", "Football", "Padel", "Tennis", "Basket-ball", "Volley-ball", "Golf", "Piscine privée");
        categoryAdapter = new CategoryAdapter(requireContext(), categories, category -> {
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
        List<Terrain> filtered = MockDataRepository.getInstance().filterTerrains(selectedCategory, searchQuery, maxPriceFilter, null);
        if (terrainAdapter != null) {
            terrainAdapter.updateData(filtered);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
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
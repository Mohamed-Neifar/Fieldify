package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.adapter.TerrainAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.TerrainDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerTerrainsFragment extends Fragment {

    private RecyclerView rvTerrains;
    private TextView tvEmpty;
    private TerrainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_terrains, container, false);

        rvTerrains = view.findViewById(R.id.rv_manager_terrains);
        tvEmpty = view.findViewById(R.id.tv_empty_manager_terrains);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_terrain);

        rvTerrains.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AddTerrainActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTerrains();
    }

    private void refreshTerrains() {
        RetrofitClient.getFieldifyApiService(requireContext()).getManagerTerrains().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TerrainDto>> call, @NonNull Response<List<TerrainDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Terrain> list = new ArrayList<>();
                    for (TerrainDto dto : response.body()) {
                        list.add(HomeFragment.mapDtoToTerrain(dto));
                    }
                    bindTerrains(list);
                } else {
                    bindTerrains(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TerrainDto>> call, @NonNull Throwable t) {
                bindTerrains(new ArrayList<>());
            }
        });
    }

    private void bindTerrains(List<Terrain> terrains) {
        if (!isAdded()) return;

        if (adapter == null) {
            adapter = new TerrainAdapter(requireContext(), terrains, new TerrainAdapter.OnTerrainClickListener() {
                @Override
                public void onTerrainClick(Terrain terrain) {
                    Intent intent = new Intent(requireContext(), TerrainDetailActivity.class);
                    intent.putExtra("TERRAIN_ID", terrain.getId());
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(Terrain terrain) {
                    MockDataRepository.getInstance().toggleFavorite(terrain.getId());
                    refreshTerrains();
                }
            });
            rvTerrains.setAdapter(adapter);
        } else {
            adapter.updateData(terrains);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(terrains.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
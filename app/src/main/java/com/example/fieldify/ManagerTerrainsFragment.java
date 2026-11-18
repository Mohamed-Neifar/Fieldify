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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ManagerTerrainsFragment extends Fragment {

    private RecyclerView rvTerrains;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private TerrainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_terrains, container, false);

        rvTerrains = view.findViewById(R.id.rv_manager_terrains);
        tvEmpty = view.findViewById(R.id.tv_empty_manager_terrains);
        fabAdd = view.findViewById(R.id.fab_add_terrain);

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
        List<Terrain> terrains = MockDataRepository.getInstance().getTerrains();

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
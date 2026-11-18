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

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private TerrainAdapter terrainAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvEmpty = view.findViewById(R.id.tv_empty_favorites);

        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavoritesList();
    }

    private void setupRecyclerView() {
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void refreshFavoritesList() {
        List<Terrain> favorites = MockDataRepository.getInstance().getFavoriteTerrains();

        if (terrainAdapter == null) {
            terrainAdapter = new TerrainAdapter(requireContext(), favorites, new TerrainAdapter.OnTerrainClickListener() {
                @Override
                public void onTerrainClick(Terrain terrain) {
                    Intent intent = new Intent(requireContext(), TerrainDetailActivity.class);
                    intent.putExtra("TERRAIN_ID", terrain.getId());
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(Terrain terrain) {
                    MockDataRepository.getInstance().toggleFavorite(terrain.getId());
                    refreshFavoritesList();
                }
            });
            rvFavorites.setAdapter(terrainAdapter);
        } else {
            terrainAdapter.updateData(favorites);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
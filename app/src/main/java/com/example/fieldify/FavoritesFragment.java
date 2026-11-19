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
import com.example.fieldify.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private TerrainAdapter terrainAdapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        sessionManager = new SessionManager(requireContext());
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
        RetrofitClient.getFieldifyApiService(requireContext()).getTerrains(null, null, null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TerrainDto>> call, @NonNull Response<List<TerrainDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Terrain> favorites = new ArrayList<>();
                    for (TerrainDto dto : response.body()) {
                        Terrain t = HomeFragment.mapDtoToTerrain(dto);
                        if (sessionManager.isFavoriteTerrain(t.getId())) {
                            t.setFavorite(true);
                            favorites.add(t);
                        }
                    }
                    bindFavorites(favorites);
                } else {
                    fallbackLocalFavorites();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TerrainDto>> call, @NonNull Throwable t) {
                fallbackLocalFavorites();
            }
        });
    }

    private void fallbackLocalFavorites() {
        List<Terrain> allTerrains = MockDataRepository.getInstance().getTerrains();
        List<Terrain> favorites = new ArrayList<>();

        for (Terrain t : allTerrains) {
            boolean isFav = sessionManager.isFavoriteTerrain(t.getId());
            t.setFavorite(isFav);
            if (isFav) {
                favorites.add(t);
            }
        }
        bindFavorites(favorites);
    }

    private void bindFavorites(List<Terrain> favorites) {
        if (!isAdded()) return;

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
                    sessionManager.toggleFavoriteTerrain(terrain.getId());
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
package com.example.fieldify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.adapter.TerrainAdapter;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.TerrainDto;
import com.example.fieldify.utils.SessionManager;
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
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_terrains, container, false);

        sessionManager = new SessionManager(requireContext());

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshTerrains();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTerrains();
    }

    private void refreshTerrains() {
        if (!isAdded()) return;

        if (sessionManager == null) {
            sessionManager = new SessionManager(requireContext());
        }

        if (sessionManager.getUserId() <= 0) {
            Toast.makeText(requireContext(), "Session expirée, veuillez vous reconnecter", Toast.LENGTH_LONG).show();
        }

        RetrofitClient.getFieldifyApiService(requireContext()).getManagerTerrains().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TerrainDto>> call, @NonNull Response<List<TerrainDto>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Terrain> list = new ArrayList<>();
                    for (TerrainDto dto : response.body()) {
                        Terrain t = HomeFragment.mapDtoToTerrain(dto);
                        t.setFavorite(sessionManager.isTerrainFavorite(t.getId()));
                        list.add(t);
                    }
                    bindTerrains(list);
                } else {
                    String errorMsg = "Erreur chargement terrains : " + response.code();
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    bindTerrains(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TerrainDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;

                String errorMsg = "Erreur chargement terrains : " + (t.getMessage() != null ? t.getMessage() : "Connexion réseau impossible");
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                bindTerrains(new ArrayList<>());
            }
        });
    }

    private void bindTerrains(List<Terrain> terrains) {
        if (!isAdded()) return;

        if (terrains != null && !terrains.isEmpty()) {
            if (rvTerrains != null) rvTerrains.setVisibility(View.VISIBLE);
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        } else {
            if (rvTerrains != null) rvTerrains.setVisibility(View.GONE);
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new TerrainAdapter(requireContext(), terrains != null ? terrains : new ArrayList<>(), new TerrainAdapter.OnTerrainClickListener() {
                @Override
                public void onTerrainClick(Terrain terrain) {
                    Intent intent = new Intent(requireContext(), TerrainDetailActivity.class);
                    intent.putExtra("TERRAIN_ID", terrain.getId());
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(Terrain terrain) {
                    sessionManager.toggleFavoriteTerrainId(terrain.getId());
                    refreshTerrains();
                }
            });
            rvTerrains.setAdapter(adapter);
        } else {
            adapter.updateData(terrains != null ? terrains : new ArrayList<>());
        }
    }
}
package com.example.fieldify;

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

import com.example.fieldify.adapter.ManagerRequestAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Reservation;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.ReservationDto;
import com.example.fieldify.network.model.UpdateReservationStatusRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerRequestsFragment extends Fragment {

    private RecyclerView rvRequests;
    private TextView tvEmpty;
    private ManagerRequestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_requests, container, false);

        rvRequests = view.findViewById(R.id.rv_manager_requests);
        tvEmpty = view.findViewById(R.id.tv_empty_manager_requests);

        rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        RetrofitClient.getFieldifyApiService(requireContext()).getManagerReservations().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationDto>> call, @NonNull Response<List<ReservationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reservation> list = new ArrayList<>();
                    for (ReservationDto dto : response.body()) {
                        list.add(MyMatchesFragment.mapReservationDto(dto));
                    }
                    bindRequests(list);
                } else {
                    bindRequests(MockDataRepository.getInstance().getReservations());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDto>> call, @NonNull Throwable t) {
                bindRequests(MockDataRepository.getInstance().getReservations());
            }
        });
    }

    private void bindRequests(List<Reservation> list) {
        if (!isAdded()) return;

        if (adapter == null) {
            adapter = new ManagerRequestAdapter(requireContext(), list, new ManagerRequestAdapter.OnRequestActionListener() {
                @Override
                public void onAccept(Reservation reservation) {
                    updateStatusOnServer(reservation, "CONFIRMED", "Réservation acceptée !");
                }

                @Override
                public void onRefuse(Reservation reservation) {
                    updateStatusOnServer(reservation, "REJECTED", "Réservation refusée.");
                }
            });
            rvRequests.setAdapter(adapter);
        } else {
            adapter.updateData(list);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void updateStatusOnServer(Reservation reservation, String status, String successMessage) {
        long resId = 1L;
        try {
            resId = Long.parseLong(reservation.getId());
        } catch (Exception ignored) {
        }

        RetrofitClient.getFieldifyApiService(requireContext()).updateReservationStatus(resId, new UpdateReservationStatusRequest(status)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                MockDataRepository.getInstance().updateReservationStatus(reservation.getId(), "CONFIRMED".equalsIgnoreCase(status) ? "Confirmé" : "Refusé");
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                refreshList();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                MockDataRepository.getInstance().updateReservationStatus(reservation.getId(), "CONFIRMED".equalsIgnoreCase(status) ? "Confirmé" : "Refusé");
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });
    }
}
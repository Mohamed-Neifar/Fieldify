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

import java.util.List;

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
        List<Reservation> reservations = MockDataRepository.getInstance().getReservations();

        if (adapter == null) {
            adapter = new ManagerRequestAdapter(requireContext(), reservations, new ManagerRequestAdapter.OnRequestActionListener() {
                @Override
                public void onAccept(Reservation reservation) {
                    MockDataRepository.getInstance().updateReservationStatus(reservation.getId(), "Confirmé");
                    Toast.makeText(requireContext(), "Réservation acceptée !", Toast.LENGTH_SHORT).show();
                    refreshList();
                }

                @Override
                public void onRefuse(Reservation reservation) {
                    MockDataRepository.getInstance().updateReservationStatus(reservation.getId(), "Refusé");
                    Toast.makeText(requireContext(), "Réservation refusée.", Toast.LENGTH_SHORT).show();
                    refreshList();
                }
            });
            rvRequests.setAdapter(adapter);
        } else {
            adapter.updateData(reservations);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(reservations.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
package com.example.fieldify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.adapter.InvitationAdapter;
import com.example.fieldify.adapter.ReservationAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Invitation;
import com.example.fieldify.model.Reservation;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MyMatchesFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView rvMatches;
    private TextView tvEmpty;

    private int selectedTabPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_matches, container, false);

        initViews(view);
        setupTabs();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCurrentTabList();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_matches);
        rvMatches = view.findViewById(R.id.rv_matches_list);
        tvEmpty = view.findViewById(R.id.tv_empty_matches);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                refreshCurrentTabList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        rvMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void refreshCurrentTabList() {
        if (selectedTabPosition == 0) {
            // Mes Réservations
            List<Reservation> resList = MockDataRepository.getInstance().getReservations();
            ReservationAdapter reservationAdapter = new ReservationAdapter(requireContext(), resList, this::showAddFriendDialog);
            rvMatches.setAdapter(reservationAdapter);
            tvEmpty.setVisibility(resList.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (selectedTabPosition == 1) {
            // Invitations reçues
            List<Invitation> invList = MockDataRepository.getInstance().getInvitations();
            InvitationAdapter invitationAdapter = new InvitationAdapter(requireContext(), invList, new InvitationAdapter.OnInvitationActionListener() {
                @Override
                public void onAccept(Invitation invitation) {
                    MockDataRepository.getInstance().acceptInvitation(invitation.getId());
                    Toast.makeText(requireContext(), "Invitation acceptée ! Match ajouté à votre agenda.", Toast.LENGTH_SHORT).show();
                    refreshCurrentTabList();
                }

                @Override
                public void onRefuse(Invitation invitation) {
                    MockDataRepository.getInstance().refuseInvitation(invitation.getId());
                    Toast.makeText(requireContext(), "Invitation refusée.", Toast.LENGTH_SHORT).show();
                    refreshCurrentTabList();
                }
            });
            rvMatches.setAdapter(invitationAdapter);
            tvEmpty.setVisibility(invList.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            // Historique
            rvMatches.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Aucun match passé dans votre historique.");
        }
    }

    private void showAddFriendDialog(Reservation reservation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Inviter un ami");

        final EditText input = new EditText(requireContext());
        input.setHint("Nom ou E-mail de l'ami");
        input.setPadding(32, 24, 32, 24);
        builder.setView(input);

        builder.setPositiveButton("Inviter", (dialog, which) -> {
            String friendName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(friendName)) {
                MockDataRepository.getInstance().addInvitedFriendToReservation(reservation.getId(), friendName);
                Toast.makeText(requireContext(), friendName + " a été invité !", Toast.LENGTH_SHORT).show();
                refreshCurrentTabList();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
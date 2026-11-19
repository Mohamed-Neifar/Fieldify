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
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.InvitationDto;
import com.example.fieldify.network.model.InviteFriendRequest;
import com.example.fieldify.network.model.ReservationDto;
import com.example.fieldify.network.model.RespondInvitationRequest;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            fetchMyReservations();
        } else if (selectedTabPosition == 1) {
            // Invitations reçues
            fetchMyInvitations();
        } else {
            // Historique
            rvMatches.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Aucun match passé dans votre historique.");
        }
    }

    private void fetchMyReservations() {
        RetrofitClient.getFieldifyApiService(requireContext()).getMyReservations().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationDto>> call, @NonNull Response<List<ReservationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reservation> list = new ArrayList<>();
                    for (ReservationDto dto : response.body()) {
                        list.add(mapReservationDto(dto));
                    }
                    bindReservations(list);
                } else {
                    bindReservations(MockDataRepository.getInstance().getReservations());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDto>> call, @NonNull Throwable t) {
                bindReservations(MockDataRepository.getInstance().getReservations());
            }
        });
    }

    private void bindReservations(List<Reservation> list) {
        if (!isAdded()) return;
        ReservationAdapter adapter = new ReservationAdapter(requireContext(), list, this::showAddFriendDialog);
        rvMatches.setAdapter(adapter);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void fetchMyInvitations() {
        RetrofitClient.getFieldifyApiService(requireContext()).getMyInvitations().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<InvitationDto>> call, @NonNull Response<List<InvitationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Invitation> list = new ArrayList<>();
                    for (InvitationDto dto : response.body()) {
                        list.add(mapInvitationDto(dto));
                    }
                    bindInvitations(list);
                } else {
                    bindInvitations(MockDataRepository.getInstance().getInvitations());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<InvitationDto>> call, @NonNull Throwable t) {
                bindInvitations(MockDataRepository.getInstance().getInvitations());
            }
        });
    }

    private void bindInvitations(List<Invitation> list) {
        if (!isAdded()) return;
        InvitationAdapter adapter = new InvitationAdapter(requireContext(), list, new InvitationAdapter.OnInvitationActionListener() {
            @Override
            public void onAccept(Invitation invitation) {
                respondToInvitation(invitation, "ACCEPTED");
            }

            @Override
            public void onRefuse(Invitation invitation) {
                respondToInvitation(invitation, "DECLINED");
            }
        });
        rvMatches.setAdapter(adapter);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void respondToInvitation(Invitation invitation, String actionStatus) {
        long invId = 1L;
        try {
            invId = Long.parseLong(invitation.getId());
        } catch (Exception ignored) {
        }

        RetrofitClient.getFieldifyApiService(requireContext()).respondInvitation(invId, new RespondInvitationRequest(actionStatus)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if ("ACCEPTED".equals(actionStatus)) {
                    MockDataRepository.getInstance().acceptInvitation(invitation.getId());
                    Toast.makeText(requireContext(), "Invitation acceptée !", Toast.LENGTH_SHORT).show();
                } else {
                    MockDataRepository.getInstance().refuseInvitation(invitation.getId());
                    Toast.makeText(requireContext(), "Invitation refusée.", Toast.LENGTH_SHORT).show();
                }
                refreshCurrentTabList();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if ("ACCEPTED".equals(actionStatus)) {
                    MockDataRepository.getInstance().acceptInvitation(invitation.getId());
                } else {
                    MockDataRepository.getInstance().refuseInvitation(invitation.getId());
                }
                refreshCurrentTabList();
            }
        });
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
                long resId = 1L;
                try {
                    resId = Long.parseLong(reservation.getId());
                } catch (Exception ignored) {
                }

                RetrofitClient.getFieldifyApiService(requireContext()).inviteFriend(resId, new InviteFriendRequest(friendName)).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        MockDataRepository.getInstance().addInvitedFriendToReservation(reservation.getId(), friendName);
                        Toast.makeText(requireContext(), friendName + " a été invité !", Toast.LENGTH_SHORT).show();
                        refreshCurrentTabList();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        MockDataRepository.getInstance().addInvitedFriendToReservation(reservation.getId(), friendName);
                        Toast.makeText(requireContext(), friendName + " a été invité !", Toast.LENGTH_SHORT).show();
                        refreshCurrentTabList();
                    }
                });
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static Reservation mapReservationDto(ReservationDto dto) {
        Reservation r = new Reservation();
        r.setId(dto.getId() != null ? String.valueOf(dto.getId()) : "r_" + System.currentTimeMillis());
        r.setDate(dto.getDate() != null ? dto.getDate() : "2026-11-24");
        r.setTimeSlot(dto.getTimeSlot() != null ? dto.getTimeSlot() : "18:00 - 19:30");
        r.setStatus(dto.getStatus());
        r.setInvitedFriends(dto.getInvitedFriends() != null ? dto.getInvitedFriends() : new ArrayList<>());
        if (dto.getTerrain() != null) {
            r.setTerrain(HomeFragment.mapDtoToTerrain(dto.getTerrain()));
        } else {
            r.setTerrain(MockDataRepository.getInstance().getTerrains().get(0));
        }
        return r;
    }

    public static Invitation mapInvitationDto(InvitationDto dto) {
        Invitation inv = new Invitation();
        inv.setId(dto.getId() != null ? String.valueOf(dto.getId()) : "i_" + System.currentTimeMillis());
        inv.setSenderName(dto.getSenderName());
        inv.setDate(dto.getDate() != null ? dto.getDate() : "2026-11-25");
        inv.setTimeSlot(dto.getTimeSlot() != null ? dto.getTimeSlot() : "20:00 - 21:30");
        inv.setStatus(dto.getStatus());
        if (dto.getTerrain() != null) {
            inv.setTerrain(HomeFragment.mapDtoToTerrain(dto.getTerrain()));
        } else {
            inv.setTerrain(MockDataRepository.getInstance().getTerrains().get(0));
        }
        return inv;
    }
}
package com.example.fieldify.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.R;
import com.example.fieldify.model.Reservation;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface OnInviteFriendClickListener {
        void onInviteFriend(Reservation reservation);
    }

    private final Context context;
    private List<Reservation> reservations;
    private final OnInviteFriendClickListener listener;

    public ReservationAdapter(Context context, List<Reservation> reservations, OnInviteFriendClickListener listener) {
        this.context = context;
        this.reservations = reservations;
        this.listener = listener;
    }

    public void updateData(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.tvTime.setText(reservation.getTimeSlot());
        holder.tvStatus.setText(reservation.getStatus());

        if ("Confirmé".equalsIgnoreCase(reservation.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status_confirmed);
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status_pending);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_primary));
        }

        String terrainName = reservation.getTerrain() != null ? reservation.getTerrain().getName() : "Espace Sportif";
        holder.tvTerrainName.setText("Match • " + terrainName);
        holder.tvDate.setText("Date : " + reservation.getDate());

        if (reservation.getInvitedFriends() != null && !reservation.getInvitedFriends().isEmpty()) {
            StringBuilder sb = new StringBuilder("Amis invités : ");
            for (int i = 0; i < reservation.getInvitedFriends().size(); i++) {
                sb.append(reservation.getInvitedFriends().get(i));
                if (i < reservation.getInvitedFriends().size() - 1) {
                    sb.append(", ");
                }
            }
            holder.tvInvitedFriends.setText(sb.toString());
            holder.tvInvitedFriends.setVisibility(View.VISIBLE);
        } else {
            holder.tvInvitedFriends.setText("Aucun ami invité pour le moment");
            holder.tvInvitedFriends.setVisibility(View.VISIBLE);
        }

        holder.btnInviteFriend.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInviteFriend(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations != null ? reservations.size() : 0;
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvStatus, tvTerrainName, tvDate, tvInvitedFriends;
        Button btnInviteFriend;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_reservation_time);
            tvStatus = itemView.findViewById(R.id.tv_reservation_status);
            tvTerrainName = itemView.findViewById(R.id.tv_reservation_terrain_name);
            tvDate = itemView.findViewById(R.id.tv_reservation_date);
            tvInvitedFriends = itemView.findViewById(R.id.tv_invited_friends);
            btnInviteFriend = itemView.findViewById(R.id.btn_invite_friend);
        }
    }
}
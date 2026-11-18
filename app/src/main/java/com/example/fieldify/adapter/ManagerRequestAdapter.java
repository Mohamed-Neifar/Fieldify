package com.example.fieldify.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.R;
import com.example.fieldify.model.Reservation;

import java.util.List;

public class ManagerRequestAdapter extends RecyclerView.Adapter<ManagerRequestAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(Reservation reservation);
        void onRefuse(Reservation reservation);
    }

    private final Context context;
    private List<Reservation> requests;
    private final OnRequestActionListener listener;

    public ManagerRequestAdapter(Context context, List<Reservation> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    public void updateData(List<Reservation> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manager_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Reservation res = requests.get(position);

        holder.tvTime.setText(res.getTimeSlot());
        holder.tvStatus.setText(res.getStatus());
        holder.tvTerrain.setText(res.getTerrain() != null ? res.getTerrain().getName() : "Espace Sportif");
        holder.tvDate.setText("Date : " + res.getDate());

        if (res.getInvitedFriends() != null && !res.getInvitedFriends().isEmpty()) {
            holder.tvUser.setText("Participants : " + String.join(", ", res.getInvitedFriends()));
        } else {
            holder.tvUser.setText("Réservation individuelle");
        }

        if ("Confirmé".equalsIgnoreCase(res.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status_confirmed);
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
            holder.layoutActions.setVisibility(View.GONE);
        } else if ("Refusé".equalsIgnoreCase(res.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_category_inactive);
            holder.tvStatus.setTextColor(Color.parseColor("#EF4444"));
            holder.layoutActions.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status_pending);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_primary));
            holder.layoutActions.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(res);
            }
        });

        holder.btnRefuse.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRefuse(res);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvStatus, tvTerrain, tvDate, tvUser;
        Button btnAccept, btnRefuse;
        LinearLayout layoutActions;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_req_time);
            tvStatus = itemView.findViewById(R.id.tv_req_status);
            tvTerrain = itemView.findViewById(R.id.tv_req_terrain);
            tvDate = itemView.findViewById(R.id.tv_req_date);
            tvUser = itemView.findViewById(R.id.tv_req_user);
            btnAccept = itemView.findViewById(R.id.btn_req_accept);
            btnRefuse = itemView.findViewById(R.id.btn_req_refuse);
            layoutActions = itemView.findViewById(R.id.layout_req_actions);
        }
    }
}
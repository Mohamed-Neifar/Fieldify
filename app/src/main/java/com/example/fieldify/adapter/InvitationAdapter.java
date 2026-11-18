package com.example.fieldify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.R;
import com.example.fieldify.model.Invitation;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {

    public interface OnInvitationActionListener {
        void onAccept(Invitation invitation);
        void onRefuse(Invitation invitation);
    }

    private final Context context;
    private List<Invitation> invitations;
    private final OnInvitationActionListener listener;

    public InvitationAdapter(Context context, List<Invitation> invitations, OnInvitationActionListener listener) {
        this.context = context;
        this.invitations = invitations;
        this.listener = listener;
    }

    public void updateData(List<Invitation> newInvitations) {
        this.invitations = newInvitations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invitation, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        Invitation invitation = invitations.get(position);

        holder.tvSender.setText(invitation.getSenderName() + " vous invite à un match !");
        String terrainName = invitation.getTerrain() != null ? invitation.getTerrain().getName() : "Espace Sportif";
        holder.tvDetails.setText(terrainName + " • " + invitation.getDate() + " • " + invitation.getTimeSlot());

        if (!"En attente".equalsIgnoreCase(invitation.getStatus())) {
            holder.btnAccept.setEnabled(false);
            holder.btnRefuse.setEnabled(false);
            holder.btnAccept.setText(invitation.getStatus());
            holder.btnRefuse.setVisibility(View.GONE);
        } else {
            holder.btnAccept.setEnabled(true);
            holder.btnRefuse.setEnabled(true);
            holder.btnAccept.setText("Accepter");
            holder.btnRefuse.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(invitation);
            }
        });

        holder.btnRefuse.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRefuse(invitation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invitations != null ? invitations.size() : 0;
    }

    static class InvitationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvDetails;
        Button btnAccept, btnRefuse;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_invitation_sender);
            tvDetails = itemView.findViewById(R.id.tv_invitation_details);
            btnAccept = itemView.findViewById(R.id.btn_accept_invitation);
            btnRefuse = itemView.findViewById(R.id.btn_refuse_invitation);
        }
    }
}
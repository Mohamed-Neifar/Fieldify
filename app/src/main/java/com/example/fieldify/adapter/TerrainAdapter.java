package com.example.fieldify.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fieldify.R;
import com.example.fieldify.model.Terrain;
import com.example.fieldify.network.RetrofitClient;

import java.util.List;

public class TerrainAdapter extends RecyclerView.Adapter<TerrainAdapter.TerrainViewHolder> {

    public interface OnTerrainClickListener {
        void onTerrainClick(Terrain terrain);
        void onFavoriteClick(Terrain terrain);
    }

    private final Context context;
    private List<Terrain> terrains;
    private final OnTerrainClickListener listener;

    public TerrainAdapter(Context context, List<Terrain> terrains, OnTerrainClickListener listener) {
        this.context = context;
        this.terrains = terrains;
        this.listener = listener;
    }

    public void updateData(List<Terrain> newTerrains) {
        this.terrains = newTerrains;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TerrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_terrain, parent, false);
        return new TerrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TerrainViewHolder holder, int position) {
        Terrain terrain = terrains.get(position);

        holder.tvName.setText(terrain.getName());
        holder.tvZone.setText(terrain.getZone());
        holder.tvRating.setText(String.valueOf(terrain.getRating()));
        holder.tvCategoryBadge.setText(terrain.getCategory());
        holder.tvPrice.setText((int) terrain.getPricePerHour() + " DT / h");

        if (terrain.getImageUrls() != null && !terrain.getImageUrls().isEmpty() && !TextUtils.isEmpty(terrain.getImageUrls().get(0))) {
            String fullUrl = RetrofitClient.getFullImageUrl(terrain.getImageUrls().get(0));
            Glide.with(context)
                    .load(fullUrl)
                    .placeholder(R.drawable.img_welcome_sports)
                    .error(R.drawable.img_welcome_sports)
                    .into(holder.ivImage);
        } else if (terrain.getPrimaryImageResId() != 0) {
            Glide.with(context)
                    .load(terrain.getPrimaryImageResId())
                    .placeholder(R.drawable.img_welcome_sports)
                    .error(R.drawable.img_welcome_sports)
                    .into(holder.ivImage);
        }

        holder.btnFavorite.setImageResource(terrain.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(terrain);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTerrainClick(terrain);
            }
        });
    }

    @Override
    public int getItemCount() {
        return terrains != null ? terrains.size() : 0;
    }

    static class TerrainViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageButton btnFavorite;
        TextView tvPrice, tvName, tvZone, tvRating, tvCategoryBadge;

        public TerrainViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_terrain_image);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            tvPrice = itemView.findViewById(R.id.tv_terrain_price);
            tvName = itemView.findViewById(R.id.tv_terrain_name);
            tvZone = itemView.findViewById(R.id.tv_terrain_zone);
            tvRating = itemView.findViewById(R.id.tv_terrain_rating);
            tvCategoryBadge = itemView.findViewById(R.id.tv_terrain_category_badge);
        }
    }
}
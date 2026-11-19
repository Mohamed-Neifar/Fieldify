package com.example.fieldify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String cleanCategory);
    }

    private final Context context;
    private final List<String> categories;
    private final OnCategoryClickListener listener;
    private String selectedCategory = "Tous";

    public CategoryAdapter(Context context, List<String> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.tvName.setText(category);

        String cleanCategory = getCleanCategory(category);
        boolean isSelected = cleanCategory.equalsIgnoreCase(getCleanCategory(selectedCategory));

        if (isSelected) {
            holder.tvName.setBackgroundResource(R.drawable.bg_chip_category_active);
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.tvName.setBackgroundResource(R.drawable.bg_chip_category_inactive);
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        holder.itemView.setOnClickListener(v -> {
            selectedCategory = category;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCategoryClick(cleanCategory);
            }
        });
    }

    public static String getCleanCategory(String formatted) {
        if (formatted == null) return "Tous";
        String clean = formatted.trim();
        if (clean.contains(" ")) {
            String[] parts = clean.split(" ");
            if (parts.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    sb.append(parts[i]).append(i == parts.length - 1 ? "" : " ");
                }
                return sb.toString().trim();
            }
        }
        return clean;
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
package com.example.fieldify.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fieldify.R;
import com.example.fieldify.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private final Context context;
    private final List<Object> imageSources;

    public ImageSliderAdapter(Context context, List<?> sources) {
        this.context = context;
        this.imageSources = new ArrayList<>();
        if (sources != null) {
            this.imageSources.addAll(sources);
        }
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Object item = imageSources.get(position);
        if (item instanceof String) {
            String url = (String) item;
            if (!TextUtils.isEmpty(url)) {
                String fullUrl = RetrofitClient.getFullImageUrl(url);
                Glide.with(context)
                        .load(fullUrl)
                        .placeholder(R.drawable.img_welcome_sports)
                        .error(R.drawable.img_welcome_sports)
                        .into(holder.imageView);
            }
        } else if (item instanceof Integer) {
            int resId = (Integer) item;
            Glide.with(context)
                    .load(resId)
                    .placeholder(R.drawable.img_welcome_sports)
                    .error(R.drawable.img_welcome_sports)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageSources != null ? imageSources.size() : 0;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}
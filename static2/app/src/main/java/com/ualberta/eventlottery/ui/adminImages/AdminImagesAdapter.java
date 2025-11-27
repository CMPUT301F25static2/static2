package com.ualberta.eventlottery.ui.adminImages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ualberta.eventlottery.model.ImageItem;
import com.ualberta.static2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying images in the admin images screen.
 * Allows admins to view and delete images.
 */
public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ImageViewHolder> {

    private List<ImageItem> imageList;
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onDeleteImage(ImageItem imageItem, int position);
        void onViewImage(ImageItem imageItem);
    }

    public AdminImagesAdapter(OnImageActionListener listener) {
        this.imageList = new ArrayList<>();
        this.listener = listener;
    }

    public void setImageList(List<ImageItem> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        imageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, imageList.size());
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem imageItem = imageList.get(position);
        holder.bind(imageItem, position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView tvImageTitle;
        private final TextView tvImageType;
        private final Button btnDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivAdminImage);
            tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
            tvImageType = itemView.findViewById(R.id.tvImageType);
            btnDelete = itemView.findViewById(R.id.btnDeleteImage);
        }

        public void bind(ImageItem imageItem, int position) {
            tvImageTitle.setText(imageItem.getTitle());
            tvImageType.setText(imageItem.getImageType());

            // Load image using Glide
            if (imageItem.getImageUrl() != null && !imageItem.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageItem.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView);
            }

            // Click to view full image
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewImage(imageItem);
                }
            });

            // Delete button
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteImage(imageItem, position);
                }
            });
        }
    }
}

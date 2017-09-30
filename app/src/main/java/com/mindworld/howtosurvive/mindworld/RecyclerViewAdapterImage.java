package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindworld.howtosurvive.mindworld.models.ImageFile;

import java.util.List;

public class RecyclerViewAdapterImage extends RecyclerView.Adapter<RecyclerViewAdapterImage.ViewHolder> {
    private Context context;
    private List<ImageFile> MainImageUploadInfoList;

    public RecyclerViewAdapterImage(Context context, List<ImageFile> imageFileList) {
        this.MainImageUploadInfoList = imageFileList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_images, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageFile imageUploadInfo = MainImageUploadInfoList.get(position);

        holder.imageNameTextView.setText(imageUploadInfo.getName());
        holder.imageLocationTextView.setText(imageUploadInfo.getLocation());

        // load image from Glide library
        Glide.with(context).load(imageUploadInfo.getURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView imageNameTextView;
        private TextView imageLocationTextView;

        private ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_image_image);

            imageNameTextView = itemView.findViewById(R.id.item_image_name);
            imageLocationTextView = itemView.findViewById(R.id.item_image_location);
        }
    }
}

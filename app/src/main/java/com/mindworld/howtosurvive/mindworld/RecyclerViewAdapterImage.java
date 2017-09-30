package com.mindworld.howtosurvive.mindworld;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mindworld.howtosurvive.mindworld.models.ImageFile;
import com.mindworld.howtosurvive.mindworld.models.TextFile;

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
        holder.imageNameTextView.setTag(position);
        // load image from Glide library
        Glide.with(context).load(imageUploadInfo.getURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

        private ImageView imageView;
        private TextView imageNameTextView;
        private TextView imageLocationTextView;

        private ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_image_image);
            imageNameTextView = itemView.findViewById(R.id.item_image_name);
            imageLocationTextView = itemView.findViewById(R.id.item_image_location);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "clicked ", Toast.LENGTH_LONG).show();
            ImageFile currentFile = MainImageUploadInfoList.get(getAdapterPosition());
            Uri path = currentFile.getUri();
            if (path != null) {
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(path, "image/*");
                // Verify that the intent will resolve to an activity
                try {
                    context.startActivity(openIntent);
                }
                catch (ActivityNotFoundException e) {
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, "long clicked ", Toast.LENGTH_LONG).show();
            ImageFile currentFile = MainImageUploadInfoList.get(getAdapterPosition());
            return true;
        }
    }
}

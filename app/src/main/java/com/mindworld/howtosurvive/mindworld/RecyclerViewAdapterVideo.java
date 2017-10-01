package com.mindworld.howtosurvive.mindworld;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mindworld.howtosurvive.mindworld.models.ImageFile;
import com.mindworld.howtosurvive.mindworld.models.TextFile;
import com.mindworld.howtosurvive.mindworld.models.VideoFile;

import java.util.List;

import static android.R.attr.path;

public class RecyclerViewAdapterVideo extends RecyclerView.Adapter<RecyclerViewAdapterVideo.ViewHolder> {
    private List<VideoFile> MainVideoUploadInfoList;
    private Context context;

    public RecyclerViewAdapterVideo(Context context, List<VideoFile> videoFileList) {
        this.MainVideoUploadInfoList = videoFileList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoFile videoUploadInfo = MainVideoUploadInfoList.get(position);

        holder.videoNameTextView.setText(videoUploadInfo.getName());
        holder.videoLocationTextView.setText(videoUploadInfo.getLocation());
    }

    @Override
    public int getItemCount() {
        return MainVideoUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView videoNameTextView;
        private TextView videoLocationTextView;

        private ViewHolder(View itemView) {
            super(itemView);

            videoNameTextView = itemView.findViewById(R.id.item_video_name);
            videoLocationTextView = itemView.findViewById(R.id.item_video_location);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            //Toast.makeText(context, "clicked ", Toast.LENGTH_LONG).show();
            VideoFile currentFile = MainVideoUploadInfoList.get(getAdapterPosition());
            if (currentFile.getUri() != null) {
                Uri path = Uri.parse(currentFile.getUri());
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(path, "video/*");
                // Verify that the intent will resolve to an activity
                try {
                    context.startActivity(openIntent);
                }
                catch (ActivityNotFoundException e) {
                }
            }
            else {
                Toast.makeText(context,"Downloading..", Toast.LENGTH_LONG).show();
                downloadFile(currentFile.getUrl());
            }
        }

        public void downloadFile(String fileUrl) {
            try {
                String servicestring = Context.DOWNLOAD_SERVICE;
                DownloadManager downloadmanager;
                downloadmanager = (DownloadManager) context.getSystemService(servicestring);
                Uri uri = Uri.parse(fileUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalFilesDir(context,
                        Environment.DIRECTORY_DOWNLOADS,"test.txt");
                downloadmanager.enqueue(request);
            }
            catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context,"Downloading..", Toast.LENGTH_LONG).show();
            VideoFile currentFile = MainVideoUploadInfoList.get(getAdapterPosition());
            downloadFile(currentFile.getUrl());
            return true;
        }
    }
}

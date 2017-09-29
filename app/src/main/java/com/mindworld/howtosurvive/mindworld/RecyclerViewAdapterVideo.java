package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mindworld.howtosurvive.mindworld.models.VideoFile;

import java.util.List;

public class RecyclerViewAdapterVideo extends RecyclerView.Adapter<RecyclerViewAdapterVideo.ViewHolder> {
    private List<VideoFile> MainVideoUploadInfoList;

    public RecyclerViewAdapterVideo(Context context, List<VideoFile> videoFileList) {
        this.MainVideoUploadInfoList = videoFileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoFile UploadInfo = MainVideoUploadInfoList.get(position);

        holder.videoNameTextView.setText(UploadInfo.getName());
    }

    @Override
    public int getItemCount() {
        return MainVideoUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView videoNameTextView;

        private ViewHolder(View itemView) {
            super(itemView);

            videoNameTextView = itemView.findViewById(R.id.item_video_name);
        }
    }
}

package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.mindworld.howtosurvive.mindworld.models.VideoFile;

import java.util.List;

public class RecyclerViewAdapterVideo extends RecyclerView.Adapter<RecyclerViewAdapterVideo.ViewHolder> {

    Context context;
    List<VideoFile> MainVideoUploadInfoList;

    public RecyclerViewAdapterVideo(Context context, List<VideoFile> TempList) {
        this.MainVideoUploadInfoList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video, parent, false);

        return  new ViewHolder(view);
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
        public TextView videoNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            videoNameTextView = itemView.findViewById(R.id.item_video_name);
        }
    }
}

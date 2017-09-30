package com.mindworld.howtosurvive.mindworld;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mindworld.howtosurvive.mindworld.models.TextFile;

import java.util.List;

public class RecyclerViewAdapterText extends RecyclerView.Adapter<RecyclerViewAdapterText.ViewHolder> {
    private List<TextFile> MainTextUploadInfoList;
    private Context context;

    public RecyclerViewAdapterText(Context context, List<TextFile> textFileList) {
        this.MainTextUploadInfoList = textFileList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_text, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextFile textUploadInfo = MainTextUploadInfoList.get(position);

        holder.textNameTextView.setText(textUploadInfo.getName());
        holder.textLocationTextView.setText(textUploadInfo.getLocation());
    }

    @Override
    public int getItemCount() {
        return MainTextUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textNameTextView;
        private TextView textLocationTextView;

        private ViewHolder(View itemView) {
            super(itemView);

            textNameTextView = itemView.findViewById(R.id.item_text_name);
            textLocationTextView = itemView.findViewById(R.id.item_text_location);
        }
    }
}

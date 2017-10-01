package com.mindworld.howtosurvive.mindworld;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textNameTextView;
        private TextView textLocationTextView;
        private String filename;

        private ViewHolder(View itemView) {
            super(itemView);

            textNameTextView = itemView.findViewById(R.id.item_text_name);
            textLocationTextView = itemView.findViewById(R.id.item_text_location);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TextFile currentFile = MainTextUploadInfoList.get(getAdapterPosition());
            filename = currentFile.getName();
            if (currentFile.getUri() != null) {
                Uri path = Uri.parse(currentFile.getUri());
                //Toast.makeText(context, "Opening", Toast.LENGTH_LONG).show();
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(path, "text/plain");
                // Verify that the intent will resolve to an activity
                try {
                    context.startActivity(openIntent);
                } catch (ActivityNotFoundException e) {
                }
            } else {
                Toast.makeText(context, "Downloading..", Toast.LENGTH_LONG).show();
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
                        Environment.DIRECTORY_DOWNLOADS, filename);
                downloadmanager.enqueue(request);
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, "Downloading..", Toast.LENGTH_LONG).show();
            TextFile currentFile = MainTextUploadInfoList.get(getAdapterPosition());
            filename = currentFile.getName();
            downloadFile(currentFile.getUrl());
            return true;
        }
    }
}

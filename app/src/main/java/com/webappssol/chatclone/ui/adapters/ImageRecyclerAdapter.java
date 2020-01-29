package com.webappssol.chatclone.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.webappssol.chatclone.R;

import java.util.List;

/**
 * Created by Dheeraj on 10,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.MyViewHolder> {

  private Context context;
  private LayoutInflater inflater;
  private List<String> urlList;

  public ImageRecyclerAdapter(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.layout_chat_image, parent, false);

    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    String url = urlList.get(position);
    Glide.with(context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .into(holder.chatMedia);

    holder.chatMedia.setOnClickListener(v -> new ImageViewer.Builder(context, urlList)
        .setStartPosition(0)
        .show());

  }

  @Override
  public int getItemCount() {
    return urlList.size();
  }

  public void setData(List<String> urlList) {
    this.urlList = urlList;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView chatMedia;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      chatMedia = itemView.findViewById(R.id.chat_image);
    }
  }
}

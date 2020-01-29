package com.webappssol.chatclone.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.webappssol.chatclone.R;

import java.util.List;

/**
 * Created by Dheeraj on 09,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class UriRecyclerAdapter extends RecyclerView.Adapter<UriRecyclerAdapter.MyViewHolder> {

  private Context context;
  private List<String> mediaUriList;
  private LayoutInflater inflater;

  public UriRecyclerAdapter(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View mediaView = inflater.inflate(R.layout.layout_media_list, parent, false);

    return new MyViewHolder(mediaView);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    Glide.with(context)
        .load(Uri.parse(mediaUriList.get(position)))
        .into(holder.mediaClip);

  }

  @Override
  public int getItemCount() {
    return mediaUriList.size();
  }

  public void setData(List<String> mediaUriList) {
    this.mediaUriList = mediaUriList;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView mediaClip;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);

      mediaClip = itemView.findViewById(R.id.media_image_clip);
    }
  }
}

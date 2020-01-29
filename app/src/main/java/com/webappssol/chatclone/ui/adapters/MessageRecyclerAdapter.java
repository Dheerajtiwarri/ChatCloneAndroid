package com.webappssol.chatclone.ui.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dheeraj on 09,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MyViewHolder> {

  private String senderId;
  private Context context;
  private List<MessageModel> messageList;
  private LayoutInflater inflater;

  public MessageRecyclerAdapter(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    Fresco.initialize(context);
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = inflater.inflate(R.layout.layout_message_list, parent, false);

    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    MessageModel messageModel = messageList.get(position);

    holder.message.setText(messageModel.getMessage());


    if (messageModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      params.gravity = Gravity.RIGHT;

      holder.message.setLayoutParams(params);
      holder.message.setBackground(context.getDrawable(R.drawable.round_corner_orange_chat));
    } else {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      params.gravity = Gravity.LEFT;

      holder.message.setLayoutParams(params);
      holder.message.setBackground(context.getDrawable(R.drawable.round_corner_app_color_chat));
    }
    // holder.sender.setText(messageModel.getSenderId());

    if (messageModel.getUrlList().size() > 0) {
      holder.imageRecycler.setVisibility(View.VISIBLE);
      initializeImageRecycler(messageModel.getUrlList(), holder.imageRecycler);
    } else {
      holder.imageRecycler.setVisibility(View.GONE);
    }




  /*  if (messageModel.getUrlList().size() > 0) {
      holder.mediaViewButton.setVisibility(View.VISIBLE);

      holder.mediaViewButton.setOnClickListener(v -> {

        new ImageViewer.Builder(context, messageModel.getUrlList())
            .setStartPosition(0)
            .show();

      });
    } else {
      holder.mediaViewButton.setVisibility(View.GONE);
    }*/

  }

  private void initializeImageRecycler(ArrayList<String> urlList, RecyclerView imageRecycler) {
    imageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(context);
    adapter.setData(urlList);
    imageRecycler.setAdapter(adapter);

  }

  @Override
  public int getItemCount() {
    return messageList.size();
  }

  public void setList(List<MessageModel> messageList) {
    this.messageList = messageList;
  }

  public void setSenderId(String creatorId) {
    this.senderId = creatorId;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView message, sender;
    LinearLayout linearLayout;
    Button mediaViewButton;

    RecyclerView imageRecycler;


    public MyViewHolder(@NonNull View itemView) {
      super(itemView);

      linearLayout = itemView.findViewById(R.id.message_recycler_container);
      message = itemView.findViewById(R.id.message_textView);
      sender = itemView.findViewById(R.id.senderId_textView);
      imageRecycler = itemView.findViewById(R.id.image_recycler_view);
      // mediaViewButton = itemView.findViewById(R.id.media_view_button);

    }
  }
}

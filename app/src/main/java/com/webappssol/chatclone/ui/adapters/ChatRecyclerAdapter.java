package com.webappssol.chatclone.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.ChatModel;

import java.util.List;

/**
 * Created by Dheeraj on 09,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MyViewHolder> {
  private static final String TAG = "ChatRecyclerAdapter";

  private Context context;
  private List<ChatModel> chatList;
  private LayoutInflater inflater;
  private View homePageRootView;

  public ChatRecyclerAdapter(Context context, View homePageRootView) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    this.homePageRootView = homePageRootView;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.layout_chat_list, parent, false);

    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    ChatModel chatModel = chatList.get(position);

    //  holder.chatTitle.setText(chatModel.getChatId());

    setUserName(holder.chatTitle, chatModel);


    holder.chatLayout.setOnClickListener(v -> {

      Bundle bundle = new Bundle();
      bundle.putString("ChatId", chatModel.getChatId());

      Navigation.findNavController(homePageRootView).navigate(R.id.chatFragment, bundle);

    });
  }

  private void setUserName(TextView chatTitle, ChatModel chatModel) {


    FirebaseDatabase.getInstance()
        .getReference()
        .child("user")
        .addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot childDetails : dataSnapshot.getChildren()) {
          Log.i(TAG, "onDataChange: list of children " + childDetails.getKey());
          if (!childDetails.getKey().equals(FirebaseAuth.getInstance().getUid())) {
            Log.i(TAG, "onDataChange: device child " + FirebaseAuth.getInstance().getUid());
            if (childDetails.hasChild("chat")) {
              for (DataSnapshot chatId : childDetails.child("chat").getChildren()) {
                if (chatId.getKey().equals(chatModel.getChatId())) {
                  Log.i(TAG, "onDataChange: user name " + childDetails.child("name").getValue().toString());
                  chatTitle.setText(childDetails.child("name").getValue().toString());
                }
              }
            }
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public int getItemCount() {
    return chatList.size();
  }

  public void setList(List<ChatModel> chatList) {
    this.chatList = chatList;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout chatLayout;
    private TextView chatTitle;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);

      chatLayout = itemView.findViewById(R.id.chat_linear_layout);
      chatTitle = itemView.findViewById(R.id.chat_title);

    }
  }
}

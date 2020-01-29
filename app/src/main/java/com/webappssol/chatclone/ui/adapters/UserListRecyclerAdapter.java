package com.webappssol.chatclone.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.UserDataModel;

import java.util.List;

/**
 * Created by Dheeraj on 05,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class UserListRecyclerAdapter extends RecyclerView.Adapter<UserListRecyclerAdapter.MyViewHolder> {

  private static final String TAG = "UserListRecyclerAdapter";

  private Context context;
  private List<UserDataModel> userList;
  private LayoutInflater inflater;
  private View userListRootView;

  private DatabaseReference mRef;


  public UserListRecyclerAdapter(Context context, View userListRootView) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.userListRootView = userListRootView;
    mRef = FirebaseDatabase.getInstance().getReference();
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = inflater.inflate(R.layout.layout_user_list, parent, false);

    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    UserDataModel userDataModel = userList.get(position);

    holder.userName.setText(userDataModel.getName());
    holder.userPhone.setText(userDataModel.getPhoneNo());

    holder.cardView.setOnClickListener(v -> {

      checkChildStatus(userDataModel);

    });

  }

  private void checkChildStatus(UserDataModel userDataModel) {

    mRef.child("user")
        .child(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild("chat")) {

              checkExistingUser(userDataModel);

            } else {
              createNewChatWindow(userDataModel);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });


  }

  private void checkExistingUser(UserDataModel userDataModel) {

    mRef.child("user")
        .child(FirebaseAuth.getInstance().getUid())
        .child("chat")
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot chatId : dataSnapshot.getChildren()) {
              Log.i(TAG, "onDataChange: " + chatId.getKey());
              mRef.child("user")
                  .child(userDataModel.getuId())
                  .child("chat")
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      for (DataSnapshot usersChatId : dataSnapshot.getChildren()) {
                        Log.i(TAG, "onDataChange: " + usersChatId.getKey());
                        if (chatId.getKey().equals(usersChatId.getKey())) {   //check id existing id's with the id's of other user..
                          ((Activity) context).onBackPressed();

                        } else {
                          createNewChatWindow(userDataModel);
                        }
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                      Log.i(TAG, "onCancelled: validation Error" + databaseError.getMessage());
                    }
                  });

            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.i(TAG, "onCancelled: " + databaseError.getMessage());
          }
        });

  }

  private void createNewChatWindow(UserDataModel userDataModel) {

    String key = mRef
        .child("chat")
        .push()
        .getKey();
    Log.i(TAG, "onBindViewHolder: " + key);

    mRef.child("user")
        .child(FirebaseAuth.getInstance().getUid())
        .child("chat")
        .child(key)
        .setValue(true);


    mRef.child("user")
        .child(userDataModel.getuId())
        .child("chat")
        .child(key)
        .setValue(true);

    ((Activity) context).onBackPressed();

  }

  @Override
  public int getItemCount() {
    return userList.size();
  }

  public void setData(List<UserDataModel> userList) {
    this.userList = userList;

  }


  public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView userName,
        userPhone;
    private MaterialCardView cardView;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);

      userName = itemView.findViewById(R.id.user_name);
      userPhone = itemView.findViewById(R.id.user_phoneNo);
      cardView = itemView.findViewById(R.id.user_details_card_view);
    }
  }
}

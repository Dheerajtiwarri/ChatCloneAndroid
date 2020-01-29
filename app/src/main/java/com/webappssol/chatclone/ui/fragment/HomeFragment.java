package com.webappssol.chatclone.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.ChatModel;
import com.webappssol.chatclone.ui.adapters.ChatRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

  private static final String TAG = "HomeFragment";
  private RecyclerView chatRecycler;
  private ChatRecyclerAdapter chatAdapter;

  private View homePageRootView;
  private ViewDataBinding binding;
  private Context context;

  private List<ChatModel> chatList;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
    homePageRootView = binding.getRoot();

    context = getContext();

    Log.i(TAG, "onCreateView: " + FirebaseAuth.getInstance().getUid());

    chatList = new ArrayList<>();

    initializeView();
    initializeRecyclerView();
    getUserChatsList();

    //getChatListWithUserDetails();

    return homePageRootView;
  }

  /**
   * not in use at this position... ;)
   */
  private void getChatListWithUserDetails() {

    FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot childDetails : dataSnapshot.getChildren()) {
          Log.i(TAG, "onDataChange: list of children " + childDetails.getKey());
          if (!childDetails.getKey().equals(FirebaseAuth.getInstance().getUid())) {
            Log.i(TAG, "onDataChange: device child " + FirebaseAuth.getInstance().getUid());
            if(childDetails.hasChild("chat")){
              for(DataSnapshot chatId : childDetails.child("chat").getChildren()){

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

  private void initializeView() {

  }


  private void loggedOut() {
    FirebaseAuth.getInstance().signOut();
    Navigation.findNavController(homePageRootView).navigate(R.id.loginFragment);
  }

  public void getUserChatsList() {
    DatabaseReference userChatDb = FirebaseDatabase
        .getInstance()
        .getReference()
        .child("user")
        .child(FirebaseAuth.getInstance().getUid())
        .child("chat");

    userChatDb.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if (dataSnapshot.exists()) {
          for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {

            ChatModel mChat = new ChatModel(childSnapShot.getKey());
            chatList.add(mChat);
            chatAdapter.setList(chatList);
            chatRecycler.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_logout:
        loggedOut();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void initializeRecyclerView() {

    chatRecycler = homePageRootView.findViewById(R.id.chat_recycler_view);
    chatRecycler.setLayoutManager(new LinearLayoutManager(context));
    chatAdapter = new ChatRecyclerAdapter(context, homePageRootView);
    //  chatAdapter.setList(chatList);
    // chatRecycler.setAdapter(chatAdapter);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.access_menu, menu);
  }
}

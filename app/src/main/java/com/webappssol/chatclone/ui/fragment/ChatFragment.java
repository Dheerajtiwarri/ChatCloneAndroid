package com.webappssol.chatclone.ui.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.MessageModel;
import com.webappssol.chatclone.ui.adapters.MessageRecyclerAdapter;
import com.webappssol.chatclone.ui.adapters.UriRecyclerAdapter;
import com.webappssol.chatclone.utils.ProjectConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

  private static final String TAG = "ChatFragment";
  int totalMediaUpload = 0;
  ArrayList<String> mediaIdList = new ArrayList<>();
  private ViewDataBinding binding;
  private View fragmentChatRootView;
  private Context context;
  private MessageRecyclerAdapter messageAdapter;
  private UriRecyclerAdapter mediaAdapter;
  private RecyclerView messageRecycler, mediaRecycler;
  private List<MessageModel> messageList;
  private List<String> mediaUriList;
  private RecyclerView.LayoutManager mLayoutManger, mediaLayoutManager;
  private String chatId;
  private DatabaseReference mDatabaseRef;
  private EditText mMessage;
  private String text = "",
      creatorId = "";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
    fragmentChatRootView = binding.getRoot();
    context = getContext();

    chatId = getArguments().getString("ChatId");
    Log.i(TAG, "onCreateView: " + chatId);

    mDatabaseRef = FirebaseDatabase
        .getInstance()
        .getReference()
        .child("chat")
        .child(chatId);


    initializeView();
    initializeRecyclerView();
    initializeMediaRecyclerView();

    getChatMessage();

    return fragmentChatRootView;
  }

  private void getChatMessage() {
    mDatabaseRef.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {


          ArrayList<String> mediaUrlList = new ArrayList<>();

          if (dataSnapshot.child("text").getValue() != null) {
            text = dataSnapshot.child("text").getValue().toString();
          }
          if (dataSnapshot.child("creator").getValue() != null) {
            creatorId = dataSnapshot.child("creator").getValue().toString();
            messageAdapter.setSenderId(creatorId);
            messageAdapter.notifyDataSetChanged();
          }
          if (dataSnapshot.child("media").getChildrenCount() > 0) {
            for (DataSnapshot mediaSnapshot : dataSnapshot.child("media").getChildren()) {
              mediaUrlList.add(mediaSnapshot.getValue().toString());
            }
          }

          MessageModel mMessage = new MessageModel(dataSnapshot.getKey(), creatorId, text, mediaUrlList);
          messageList.add(mMessage);
          mLayoutManger.scrollToPosition(messageList.size() - 1);
          messageAdapter.notifyDataSetChanged();

        }
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }

  private void initializeView() {


    Button sendButton = fragmentChatRootView.findViewById(R.id.send_button);
    sendButton.setOnClickListener(v -> {
      sendMessage();
    });

    Button mediaButton = fragmentChatRootView.findViewById(R.id.media_button);
    mediaButton.setOnClickListener(v -> {
      openGallery();
    });

  }

  private void openGallery() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    intent.setAction(Intent.ACTION_GET_CONTENT);

    startActivityForResult(Intent.createChooser(intent, "select Picture(s)"), ProjectConstant.OPEN_GALLERY);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == getActivity().RESULT_OK) {
      if (requestCode == ProjectConstant.OPEN_GALLERY) {

        if (data.getClipData() == null) {
          mediaUriList.add(data.getData().toString());
        } else {
          for (int i = 0; i < data.getClipData().getItemCount(); i++) {
            mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
          }
        }
        mediaAdapter.notifyDataSetChanged();
      }
    }
  }

  private void sendMessage() {


    ProgressDialog mProgressDialog = new ProgressDialog(context);
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setMessage("Loading...");
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();


    mMessage = fragmentChatRootView.findViewById(R.id.message_box);

    String messageId = mDatabaseRef.push().getKey();
    DatabaseReference newMessageDb = mDatabaseRef
        .child(messageId);

    final Map newMessageMap = new HashMap();

    newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

    if (!mMessage.getText().toString().isEmpty())
      newMessageMap.put("text", mMessage.getText().toString());

    //   newMessageDb.updateChildren(newMessageMap);

    if (!mediaUriList.isEmpty()) {
      for (String mediaUri : mediaUriList) {

        String mediaId = newMessageDb
            .child("media")
            .push()
            .getKey();
        mediaIdList.add(mediaId);
        Log.i(TAG, "sendMessage: " + mediaId);

        final StorageReference filePath = FirebaseStorage.getInstance()
            .getReference()
            .child("chat")
            .child(chatId)
            .child(messageId)
            .child(mediaId);

        UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

        uploadTask.addOnSuccessListener(taskSnapshot -> {
          filePath.getDownloadUrl().addOnSuccessListener(uri -> {

            newMessageMap.put("/media/" + mediaIdList.get(totalMediaUpload) + "/", uri.toString());

            totalMediaUpload++;
            if (totalMediaUpload == mediaUriList.size()) {
              updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
              mProgressDialog.dismiss();
            }
          });
        });


      }
    } else {
      if (!mMessage.getText().toString().isEmpty()) {
        mProgressDialog.dismiss();
        updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
      }
    }

    //  mMessage.setText(null);

  }

  private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap) {

    newMessageDb.updateChildren(newMessageMap);
    mMessage.setText(null);
    mediaIdList.clear();
    mediaUriList.clear();
    mediaAdapter.notifyDataSetChanged();

  }

  private void initializeMediaRecyclerView() {

    mediaUriList = new ArrayList<>();
    mediaRecycler = fragmentChatRootView.findViewById(R.id.media_recycler_view);
    mediaLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
    mediaRecycler.setLayoutManager(mediaLayoutManager);
    mediaAdapter = new UriRecyclerAdapter(context);
    mediaAdapter.setData(mediaUriList);
    mediaRecycler.setAdapter(mediaAdapter);

  }


  private void initializeRecyclerView() {

    messageList = new ArrayList<>();
    messageRecycler = fragmentChatRootView.findViewById(R.id.message_recycler_view);
    mLayoutManger = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
    messageRecycler.setLayoutManager(mLayoutManger);
    messageAdapter = new MessageRecyclerAdapter(context);
    messageAdapter.setList(messageList);
    messageRecycler.setAdapter(messageAdapter);
  }

}

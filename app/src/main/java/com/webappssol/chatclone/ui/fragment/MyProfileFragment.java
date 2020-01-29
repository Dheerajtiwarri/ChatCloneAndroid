package com.webappssol.chatclone.ui.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
  private static final String TAG = "MyProfileFragment";

  private DatabaseReference mDatabase;
  private View fragmentProfileView;
  private Button updateBtn;
  private TextView phoneTextView;
  private EditText nameEditText;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    fragmentProfileView = inflater.inflate(R.layout.fragment_my_profile, container, false);

    mDatabase = FirebaseDatabase
        .getInstance()
        .getReference()
        .child("user");

    initialize();
    getStoredValue();

    return fragmentProfileView;
  }

  private void initialize() {

    phoneTextView = fragmentProfileView.findViewById(R.id.phone_number_textView);
    nameEditText = fragmentProfileView.findViewById(R.id.name_editText);

    updateBtn = fragmentProfileView.findViewById(R.id.update_button);

    updateBtn.setOnClickListener(v -> {
      updateUserInfo();
    });

  }

  private void updateUserInfo() {
    if (!nameEditText.getText().equals("")) {
      mDatabase
          .child(FirebaseAuth.getInstance().getUid())
          .child("name")
          .setValue(nameEditText.getText().toString())
          .addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
          });

    }
  }

  public void getStoredValue() {

    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot userDetails : dataSnapshot.getChildren()) {

          Log.i(TAG, "onDataChange: key check" + userDetails.getKey() + "\n \n" + FirebaseAuth.getInstance().getUid());
          if (userDetails.getKey().equals(FirebaseAuth.getInstance().getUid())) {
            for (DataSnapshot userData : userDetails.getChildren()) {
              Log.i(TAG, "onDataChange: user data" + userData.getValue());
              if (userData.getKey().equals("phone")) {
                phoneTextView.setText(userData.getValue().toString());
              } else if (userData.getKey().equals("name")) {
                nameEditText.setText(userData.getValue().toString());
                nameEditText.setSelection(nameEditText.getText().length());
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


}

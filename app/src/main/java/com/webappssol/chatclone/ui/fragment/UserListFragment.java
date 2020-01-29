package com.webappssol.chatclone.ui.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;
import com.webappssol.chatclone.models.UserDataModel;
import com.webappssol.chatclone.ui.adapters.UserListRecyclerAdapter;
import com.webappssol.chatclone.utils.CountryToPhonePrefix;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {
  private static final String TAG = "UserListFragment";

  private View userListRootView;
  private ViewDataBinding binding;
  private RecyclerView userListView;

  private UserListRecyclerAdapter userListAdapter;

  private Context context;

  private List<UserDataModel> deviceContactList, appUserList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater,
        R.layout.fragment_user_list,
        container,
        false);

    userListRootView = binding.getRoot();
    context = getContext();

    deviceContactList = new ArrayList<>();
    appUserList = new ArrayList<>();


    setAdapter();
    getContactList();

    return userListRootView;
  }


  /**
   * get contact list from device..
   * replace all extra data from phone..
   */
  private void getContactList() {
    Log.i(TAG, "getContactList: i am in... :) ");
    String isoPrefix = "+91";

    Cursor phones = getActivity().getContentResolver()
        .query(ContactsContract
                .CommonDataKinds
                .Phone.CONTENT_URI,
            null,
            null,
            null,
            null);


    while (phones.moveToNext()) {
      String name = phones.getString(phones
          .getColumnIndex(ContactsContract
              .CommonDataKinds
              .Phone.DISPLAY_NAME));

      String phoneNumber = phones.getString(phones
          .getColumnIndex(ContactsContract
              .CommonDataKinds
              .Phone.NUMBER));

      phoneNumber = phoneNumber.replace(" ", "");
      phoneNumber = phoneNumber.replace("-", "");
      phoneNumber = phoneNumber.replace("(", "");
      phoneNumber = phoneNumber.replace(")", "");

      if (!String.valueOf(phoneNumber.charAt(0)).equals("+")) {
        phoneNumber = isoPrefix + phoneNumber;
      }

      UserDataModel contactUser = new UserDataModel("", name, phoneNumber);
      deviceContactList.add(contactUser);
      userListAdapter.notifyDataSetChanged();
      getUserDetailOnContacts(contactUser);
    }
  }

  /**
   * getting user who have stored in database...
   *
   * @param user
   */
  private void getUserDetailOnContacts(UserDataModel user) {
    Log.i(TAG, "getUserDetailOnContacts: " + user.getName() + " " + user.getPhoneNo());
    DatabaseReference mUserDb = FirebaseDatabase
        .getInstance()
        .getReference()
        .child("user");

    Query query = mUserDb
        .orderByChild("phone")
        .equalTo(user.getPhoneNo());

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String uId = "",
              phone = "",
              name = "";
          for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            if (childSnapshot.child("phone").getValue() != null &&
                childSnapshot.child("name").getValue() != null) {
              phone = childSnapshot.child("phone").getValue().toString();
              name = childSnapshot.child("name").getValue().toString();
              uId = childSnapshot.getKey();
            }

            UserDataModel appUser = new UserDataModel(uId, name, phone);

            //set phone number with user name...
            if (name.equals(phone)) {
              for (UserDataModel contactIterator : deviceContactList) {
                if (contactIterator.getPhoneNo().equals(appUser.getPhoneNo())) {
                  appUser.setName(contactIterator.getName());
                }
              }
            }

            appUserList.add(appUser);
            userListAdapter.notifyDataSetChanged();
            Log.i(TAG, "onDataChange: " + appUserList.size());
            return;
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }


  /**
   * use to get country code..
   *
   * @return
   */

  //TODO problem in iso code.. :(
  private String getCountryIso() {
    Log.i(TAG, "getCountryIso: i am in... :)");
    String iso = "+91";

    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    if (telephonyManager.getNetworkCountryIso() != null) {
      if (!telephonyManager.getNetworkCountryIso().equals("")) {
        iso = telephonyManager.getNetworkCountryIso();
      }
    }
    Log.i(TAG, "getCountryIso: " + iso);
    return CountryToPhonePrefix.getPhone(iso);
  }


  /**
   * perform recycler view operation
   */
  private void setAdapter() {
    Log.i(TAG, "setAdapter: I am in... :) ");
    userListView = userListRootView.findViewById(R.id.user_list_recycler_view);
    userListView.setLayoutManager(new LinearLayoutManager(context));
    userListAdapter = new UserListRecyclerAdapter(context,userListRootView);
    userListAdapter.setData(appUserList);
    userListView.setAdapter(userListAdapter);

  }

}

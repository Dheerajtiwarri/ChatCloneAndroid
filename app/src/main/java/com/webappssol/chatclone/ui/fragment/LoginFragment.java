package com.webappssol.chatclone.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.webappssol.chatclone.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * use to login
 * right now use firebase to login...
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

  private View loginPageRootView;
  private ViewDataBinding binding;
  private ImageView appIconImageView;
  private TextView phoneNoTextView, passwordTextView;
  private Button loginButton;
  private LinearLayout verifyCodeLinearLayout;
  private Context context;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
  private String verificationCode;

  public static void hideKeyboard(Activity activity) {
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window token from it.
    View view = activity.getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
    loginPageRootView = binding.getRoot();
    context = getContext();


    // userIsLogIn();

    initializeView();


    return loginPageRootView;
  }

  /**
   * login operation.
   */
  private void userIsLogIn() {
    hideKeyboard(getActivity());
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      phoneNoTextView.setText("");
      passwordTextView.setText("");
      Navigation.findNavController(loginPageRootView).navigate(R.id.homeFragment);
    }
  }

  /**
   * initialize views
   */
  private void initializeView() {
    appIconImageView = loginPageRootView.findViewById(R.id.login_icon_imageView);
    phoneNoTextView = loginPageRootView.findViewById(R.id.login_phone_editText);
    passwordTextView = loginPageRootView.findViewById(R.id.login_password_editText);

    loginButton = loginPageRootView.findViewById(R.id.login_button);

    verifyCodeLinearLayout = loginPageRootView.findViewById(R.id.verify_code_linear_layout);

    loginButton.setOnClickListener(this::startPhoneVerification);

    //use to authenticate the user..
    mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      @Override
      public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        signInWithPhoneAuthCredential(phoneAuthCredential);
      }

      @Override
      public void onVerificationFailed(@NonNull FirebaseException e) {

      }

      @Override
      public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(verificationId, forceResendingToken);
        verificationCode = verificationId;
        verifyCodeLinearLayout.setVisibility(View.VISIBLE);
        loginButton.setText("Verify Code");
      }
    };


    Glide.with(context)
        .load(context.getDrawable(R.drawable.shinchan))
        .apply(RequestOptions.circleCropTransform())
        .into(appIconImageView);

  }

  /**
   * on auto verification fail
   * then use to login using verification code...
   */
  private void verifyPhoneNumberWithCode() {
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, passwordTextView.getText().toString());
    signInWithPhoneAuthCredential(credential);
  }

  /**
   * use to sign in using mCallback response
   *
   * @param phoneAuthCredential
   */
  private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(getActivity(), task -> {
      if (task.isSuccessful()) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
          final DatabaseReference mRef = FirebaseDatabase
              .getInstance()
              .getReference()
              .child("user")
              .child(user.getUid());
          mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (!dataSnapshot.exists()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("phone", user.getPhoneNumber());
                userMap.put("name", user.getPhoneNumber());
                mRef.updateChildren(userMap);
              }
              userIsLogIn();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        }


        // userIsLogIn();
      }
    });

  }

  /**
   * phone verification started..
   *
   * @param view
   */
  private void startPhoneVerification(View view) {
    if (verificationCode != null) {
      verifyPhoneNumberWithCode();
    } else {
      PhoneAuthProvider.getInstance().verifyPhoneNumber(
          "+91" + phoneNoTextView.getText().toString(),
          60,
          TimeUnit.SECONDS,
          getActivity(),
          mCallback);
    }

  }

}

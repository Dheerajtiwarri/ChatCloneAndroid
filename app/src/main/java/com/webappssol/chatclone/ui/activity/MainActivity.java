package com.webappssol.chatclone.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.webappssol.chatclone.R;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

  String[] permissions = {Manifest.permission.CAMERA,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.CALL_PHONE,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.INTERNET,
      Manifest.permission.FOREGROUND_SERVICE};


  private NavController navController;
  private AppBarConfiguration appBarConfiguration;
  private BottomNavigationView bottomNavigationView;
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FirebaseApp.getInstance();


    navController = Navigation.findNavController(this, R.id.main_activity_fragment);
    appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();


    if (!hasPermissions(this, permissions)) {
      ActivityCompat.requestPermissions(this, permissions, 0);//TODO set permission on ProjectConstant
    }


    initializeView();
    setToolbar();
    setDestinationChangeAction();
    setMenuItemClick();

  }

  private void initializeView() {

    bottomNavigationView = findViewById(R.id.bottom_navigation);

  }

  private void setToolbar() {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }


  private void setDestinationChangeAction() {
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      switch (destination.getId()) {
        case R.id.loginFragment:
          bottomNavigationView.setVisibility(View.GONE);
          break;
        case R.id.homeFragment:
          setToolbarText("Chat");
          break;
        case R.id.userListFragment:
          setToolbarText("Contacts");
          break;
        case R.id.myProfileFragment:
          setToolbarText("My Profile");
          break;
        case R.id.chatFragment:
          bottomNavigationView.setVisibility(View.GONE);
          break;
      }
    });
  }

  private void setToolbarText(String title) {
    bottomNavigationView.setVisibility(View.VISIBLE);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setTitle(title);

  }

  private void setMenuItemClick() {

    bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
      selectedDestination(menuItem);
      return true;
    });
  }

  private void selectedDestination(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.bottom_home:
        navController.navigate(R.id.homeFragment);
        break;
      case R.id.bottom_contacts:
        navController.navigate(R.id.userListFragment);
        break;
      case R.id.bottom_profile:
        navController.navigate(R.id.myProfileFragment);
        break;
    }

  }


  /**
   * if permission is not allowed the it request for permission...
   *
   * @param context
   * @param permissions
   * @return
   */
  public boolean hasPermissions(Context context, String... permissions) {
    if (Build.VERSION.SDK_INT >= M && context != null && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public void onBackPressed() {
    if (navController.getCurrentDestination().getId() == R.id.loginFragment || navController.getCurrentDestination().getId() == R.id.homeFragment) {
      finish();
    } else if (navController.getCurrentDestination().getId() == R.id.userListFragment ||
        navController.getCurrentDestination().getId() == R.id.myProfileFragment ||
        navController.getCurrentDestination().getId() == R.id.chatFragment) {
      bottomNavigationView.setSelectedItemId(R.id.bottom_home);
      navController.navigate(R.id.homeFragment);
    } else {
      super.onBackPressed();
    }


  }
}

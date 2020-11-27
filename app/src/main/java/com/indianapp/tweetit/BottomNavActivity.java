package com.indianapp.tweetit;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavActivity extends AppCompatActivity {

    Fragment selectedFragment;
    String fragName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        selectedFragment = null;
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new FeedFrag(), "home").commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Integer menuItem = item.getItemId();
            switch (menuItem) {
                case R.id.homeNav:
                    selectedFragment = new FeedFrag();
                    fragName = "home";
                    break;
                case R.id.search:
                    selectedFragment = new UserFrag();
                    fragName = "search";
                    break;
                case R.id.following:
                    selectedFragment = new FollowingFrag();
                    fragName = "following";
                    break;
                case R.id.userNav:
                    selectedFragment = new YourTweetFrag();
                    fragName = "profile";
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, selectedFragment, String.valueOf(fragName)).commit();
            return true;
        }
    };


    @Override
    public void onBackPressed() {

        if (selectedFragment == null) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}
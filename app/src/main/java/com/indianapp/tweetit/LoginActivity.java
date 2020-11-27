package com.indianapp.tweetit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEdit;
    EditText passwordEdit;

    public void redirect() {
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void loginl(View view) {
        usernameEdit = findViewById(R.id.usernameEditl);
        passwordEdit = findViewById(R.id.passwordEditl);
        final String username = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    redirect();
                } else {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "LogIn" + "</font>"));
    }
}
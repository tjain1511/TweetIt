package com.indianapp.tweetit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

public class SignupActivity extends AppCompatActivity {

    EditText usernameEdit;
    EditText passwordEdit;
    EditText name;

    public void redirect() {
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signups(View view) {
        name = findViewById(R.id.name);
        usernameEdit = findViewById(R.id.usernameEdits);
        passwordEdit = findViewById(R.id.passwordEdits);
        final String username = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        ParseUser newuser = new ParseUser();
        newuser.setUsername(username);
        newuser.setPassword(password);
        newuser.put("name", name.getText().toString());

        newuser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    redirect();
                } else {
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "SignUp" + "</font>"));

    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("image.png", byteArray);
                ParseUser.getCurrentUser().put("image", file);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(SignupActivity.this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                            Intent mintent = new Intent(getApplicationContext(), BottomNavActivity.class);
                            startActivity(mintent);
                            overridePendingTransition(0, 0);
                            finish();
                        } else {
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this, "there has been issue uploading an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }
}
package com.indianapp.tweetit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {
    private GifImageView gifImageView;
    GifImageView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        textView = findViewById(R.id.textView);
        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        try {
            InputStream inputStream = getAssets().open("twitter.gif");
            final byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
            gifImageView.setTranslationX(-800);
            gifImageView.animate().translationXBy(800).setDuration(1000);
            InputStream inputStrea = getAssets().open("name.gif");
            final byte[] bytei = IOUtils.toByteArray(inputStrea);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setBytes(bytei);
                    textView.startAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textView.stopAnimation();
                        }
                    }, 550);

                }
            }, 1000);

        } catch (IOException ex) {
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, MainActivity.class));
                SplashScreen.this.overridePendingTransition(0, 0);
                SplashScreen.this.finish();
            }
        }, 3000); // 3000 = 3seconds
    }
}


package com.indianapp.tweetit;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;

public class YourTweetFrag extends Fragment {
    View rootView;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<Integer> likes = new ArrayList<>();
    Bitmap bitmappr;
    ArrayList<List<Object>> likedby = new ArrayList<List<Object>>();
    private static final String TAG = "MainActivity";
    ImageView imageView;
    CardView cardView;
    TextView postspr;
    TextView likespr;
    TextView followingpr;
    String nametxt;
    Integer totallikes;
    Integer totalPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_your_tweet, container, false);
        return rootView;
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);


    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.i("pandey", String.valueOf(byteArray));
                ParseFile file = new ParseFile("image.png", byteArray);
                ParseUser.getCurrentUser().put("image", file);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new YourTweetFrag()).commit();

                        } else {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "there has been issue uploading an image", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new YourTweetFrag()).commit();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {

            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.customview);
            dialog.setTitle("Title...");
            final EditText tweetText = dialog.findViewById(R.id.tweetText);

            Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            Button sendButton = dialog.findViewById(R.id.send);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseObject tweet = new ParseObject("Tweet");
                    tweet.put("tweet", tweetText.getText().toString());
                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new YourTweetFrag()).commit();

                                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            });
            dialog.show();

        } else if (id == R.id.logout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Are you sure want to log out?").setTitle("Logout").setIcon(R.drawable.ic_baseline_login_24_red)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            ParseUser.logOut();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                        }
                    }).setNegativeButton("Cancel", null);


            AlertDialog alert1 = alert.create();
            alert1.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\" >" + "@" + ParseUser.getCurrentUser().getUsername() + "</font>"));
        initName();

    }

    private void initName() {
        totallikes = 0;
        totalPosts = 0;
        postspr = rootView.findViewById(R.id.postspr);
        likespr = rootView.findViewById(R.id.likespr);
        followingpr = rootView.findViewById(R.id.followingpr);
        TextView namepr = rootView.findViewById(R.id.namepr);
        nametxt = ParseUser.getCurrentUser().getString("name").substring(0, 1).toUpperCase() + ParseUser.getCurrentUser().getString("name").substring(1).toLowerCase();
        namepr.setText(nametxt);
        followingpr.setText(String.valueOf(ParseUser.getCurrentUser().getList("isFollowing").size()));

        final ProgressBar progressbar = rootView.findViewById(R.id.progressBar);
        final CardView cardViewq = rootView.findViewById(R.id.cardView4);
        imageView = rootView.findViewById(R.id.upload);
        cardView = rootView.findViewById(R.id.uploadp);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();

                }
            }
        });

        final ParseQuery<ParseObject> twQuery = ParseQuery.getQuery("Tweet");
        twQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        twQuery.orderByDescending("createdAt");
        twQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mNames.clear();
                    likedby.clear();
                    for (ParseObject tweets : objects) {
                        Date date = tweets.getCreatedAt();
                        String stringDate = DateFormat.getDateInstance().format(date);
                        String stringTime = DateFormat.getTimeInstance().format(date);
                        likedby.add(tweets.getList("likedby"));
                        totallikes += tweets.getList("likedby").size();
                        mNames.add(tweets.getString("tweet"));

                        likes.add(tweets.getInt("likes"));
                        mDate.add(stringDate);
                        mTime.add(stringTime);

                    }
                    totalPosts += mNames.size();
                    postspr.setText(String.valueOf(totalPosts));
                    likespr.setText(String.valueOf(totallikes));

                } else {
                }
                ParseQuery<ParseUser> iquery = ParseUser.getQuery();
                iquery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                iquery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            ParseFile file = (ParseFile) objects.get(0).get("image");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        bitmappr = bitmap;
                                        imageView.setImageBitmap(bitmap);
                                        initRecylerView();
                                        progressbar.setVisibility(View.INVISIBLE);
                                        cardViewq.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
            }

        });
    }


    private void initRecylerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycelrView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mDate, mTime, likes, likedby, bitmappr, nametxt, likespr, postspr);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}

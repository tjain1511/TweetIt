package com.indianapp.tweetit;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FeedFrag extends Fragment {
    View rootView;
    ProgressBar progressBar;
    CircleImageView yourpic;
    TextView whatsname;
    CardView uploadp;
    private ArrayList<String> mNamesfeed = new ArrayList<>();
    private ArrayList<String> mUserNamesfeed = new ArrayList<>();
    private ArrayList<String> mDatefeed = new ArrayList<>();
    private ArrayList<String> mTimefeed = new ArrayList<>();
    private ArrayList<Integer> likesfeed = new ArrayList<>();
    Bitmap bitmapprfeed;
    ArrayList<List<Object>> likedbyfeed = new ArrayList<List<Object>>();
    Map<String, Bitmap> usersImage = new HashMap<>();
    Map<String, String> usersName = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_feed, container, false);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add, menu);
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

        }
        return super.onOptionsItemSelected(item);
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
                ParseFile file = new ParseFile("image.png", byteArray);
                ParseUser.getCurrentUser().put("image", file);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "Image has been Shared!", Toast.LENGTH_SHORT).show();
                            Intent mintent = new Intent(getContext(), BottomNavActivity.class);
                            startActivity(mintent);
                        } else {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "there has been issue uploading an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startActivity(new Intent(getContext(), BottomNavActivity.class));
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
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Your Feed" + "</font>"));

        uploadp = rootView.findViewById(R.id.uploadp);
        uploadp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();

                }

            }
        });
        CardView cardView2 = rootView.findViewById(R.id.cardView2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        initName();

    }

    private void initName() {
        final CardView cardView = rootView.findViewById(R.id.cardView4);
        whatsname = rootView.findViewById(R.id.textView7);
        String name = ParseUser.getCurrentUser().getString("name");
        whatsname.setText("What's on your mind, " + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        progressBar = rootView.findViewById(R.id.progressBar);
        yourpic = rootView.findViewById(R.id.upload);

        final ParseQuery<ParseObject> twQuery = ParseQuery.getQuery("Tweet");
        twQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        twQuery.orderByDescending("createdAt");
        twQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (ParseObject tweets : objects) {
                        if (ParseUser.getCurrentUser().getList("isFollowing").contains(tweets.get("username"))) {
                            Date date = tweets.getCreatedAt();
                            String stringDate = DateFormat.getDateInstance().format(date);
                            String stringTime = DateFormat.getTimeInstance().format(date);
                            likedbyfeed.add(tweets.getList("likedby"));
                            mNamesfeed.add(tweets.getString("tweet"));
                            likesfeed.add(tweets.getInt("likes"));
                            mDatefeed.add(stringDate);
                            mTimefeed.add(stringTime);
                            mUserNamesfeed.add(tweets.getString("username"));
                        }

                    }
                } else {
                }
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(final List<ParseUser> objects, ParseException e) {
                        if (e == null && objects.size() > 0) {
                            for (final ParseUser user : objects) {
                                ParseFile file = (ParseFile) user.get("image");
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null && data != null) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            usersImage.put(user.getUsername(), bitmap);
                                            usersName.put(user.getUsername(), user.getString("name"));
                                            if (usersImage.size() == objects.size()) {
                                                initRecylerView();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                cardView.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    }
                                });

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
                                                    yourpic.setVisibility(View.VISIBLE);
                                                    yourpic.setImageBitmap(bitmap);


                                                }
                                            }
                                        });

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
        RecyclerView recyclerView = rootView.findViewById(R.id.recycelrViewfeed);
        RecyclerVIewAdapterfeed adapter = new RecyclerVIewAdapterfeed(getActivity(), mNamesfeed, mDatefeed, mTimefeed, likesfeed, likedbyfeed, bitmapprfeed, mUserNamesfeed, usersImage, usersName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}

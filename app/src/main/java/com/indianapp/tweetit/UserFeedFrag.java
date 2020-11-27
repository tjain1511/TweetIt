package com.indianapp.tweetit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserFeedFrag extends Fragment {
    View rootView;
    private ArrayList<String> mNamesuf = new java.util.ArrayList<>();
    private List<Object> isFollowinguf = new ArrayList<>();
    private ArrayList<String> mDateuf = new ArrayList<>();
    private ArrayList<String> mTimeuf = new ArrayList<>();
    private ArrayList<Integer> likesuf = new ArrayList<>();
    ProgressBar progressBar;
    Bitmap bitmappruf;
    ArrayList<List<Object>> likedbyuf = new ArrayList<List<Object>>();
    ImageView imageViewuf;
    TextView isFollowinguft;
    String ufn;
    TextView ufnt;
    TextView ufp;
    TextView uff;
    TextView ufl;
    CardView cardView;
    CardView usercardf;
    Integer totallike;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_user_feed, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final String activity = getArguments().getString("activity");
        final String username1 = getArguments().getString("message");
        final String name1 = getArguments().getString("name");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "@" + username1 + "</font>"));
        usercardf = rootView.findViewById(R.id.usercardf);
        ;
        final String username = getArguments().getString("message");
        isFollowinguft = rootView.findViewById(R.id.isFollowinguf);
        isFollowinguf = ParseUser.getCurrentUser().getList("isFollowing");
        if (isFollowinguf.contains(username)) {
            isFollowinguft.setText("Following");
            isFollowinguft.setTextColor(Color.rgb(0, 0, 0));
            usercardf.setCardBackgroundColor(Color.rgb(255, 255, 255));
        } else {
            isFollowinguft.setText("Follow");
            isFollowinguft.setTextColor(Color.rgb(255, 255, 255));
            usercardf.setCardBackgroundColor(Color.rgb(29, 161, 242));
        }
        isFollowinguft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowinguft.getText().toString().equals("Follow")) {
                    isFollowinguft.setTextColor(Color.rgb(0, 0, 0));
                    usercardf.setCardBackgroundColor(Color.rgb(255, 255, 255));
                    isFollowinguft.setText("Following");
                    ParseUser.getCurrentUser().add("isFollowing", username);
                } else {
                    isFollowinguft.setText("Follow");
                    isFollowinguft.setTextColor(Color.rgb(255, 255, 255));
                    usercardf.setCardBackgroundColor(Color.rgb(29, 161, 242));
                    ParseUser.getCurrentUser().getList("isFollowing").remove(username);
                    List tem = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", tem);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });
        initName();

    }

    private void initName() {
        cardView = rootView.findViewById(R.id.cardView4uf);
        totallike = 0;
        progressBar = rootView.findViewById(R.id.progressBar);
        ufnt = rootView.findViewById(R.id.ufn);
        uff = rootView.findViewById(R.id.uff);
        ufl = rootView.findViewById(R.id.ufl);
        ufp = rootView.findViewById(R.id.ufp);
        final String username = getArguments().getString("message");
        imageViewuf = rootView.findViewById(R.id.uploaduf);
        final ParseQuery<ParseObject> twQuery = ParseQuery.getQuery("Tweet");
        twQuery.whereEqualTo("username", username);
        twQuery.orderByDescending("createdAt");
        twQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject tweets : objects) {
                        Date date = tweets.getCreatedAt();
                        String stringDate = DateFormat.getDateInstance().format(date);
                        String stringTime = DateFormat.getTimeInstance().format(date);
                        likedbyuf.add(tweets.getList("likedby"));
                        mNamesuf.add(tweets.getString("tweet"));
                        likesuf.add(tweets.getInt("likes"));
                        mDateuf.add(stringDate);
                        totallike += tweets.getList("likedby").size();
                        mTimeuf.add(stringTime);
                    }
                    ufl.setText(String.valueOf(totallike));
                    ufp.setText(String.valueOf(mNamesuf.size()));
                } else {
                }
                ParseQuery<ParseUser> iquery = ParseUser.getQuery();
                iquery.whereEqualTo("username", username);
                iquery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(final List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            ParseFile file = (ParseFile) objects.get(0).get("image");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        bitmappruf = bitmap;
                                        imageViewuf.setImageBitmap(bitmap);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        cardView.setVisibility(View.VISIBLE);
                                        ufn = objects.get(0).getString("name");
                                        ufnt.setText(ufn.substring(0, 1).toUpperCase() + ufn.substring(1).toLowerCase());

                                        isFollowinguf = ParseUser.getCurrentUser().getList("isFollowing");
                                        uff.setText(String.valueOf(isFollowinguf.size()));
                                        initRecylerView();
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
        final String username = getArguments().getString("message");
        final String name = getArguments().getString("name");
        RecyclerView recyclerView = rootView.findViewById(R.id.recycelrViewuf);
        RecyclerViewAdapteruserfeed adapter = new RecyclerViewAdapteruserfeed(getActivity(), mNamesuf, mDateuf, mTimeuf, likesuf, likedbyuf, bitmappruf, username, isFollowinguf, name, ufl);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}

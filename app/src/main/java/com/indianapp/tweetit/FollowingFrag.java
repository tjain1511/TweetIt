package com.indianapp.tweetit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowingFrag extends Fragment {
    View rootView;
    List<Object> following = new ArrayList<>();


    ArrayList<String> followingf = new ArrayList<>();
    ArrayList<Bitmap> profileusersf = new ArrayList<>();
    Map<String, Bitmap> profileinfof = new HashMap<>();
    Map<String, String> profilename = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_following, container, false);
        return rootView;

    }

    private void initName() {
        final ProgressBar progressbar = rootView.findViewById(R.id.progressBar);
        String username1 = ParseUser.getCurrentUser().getUsername();
        Log.i("current user",username1);
        following = ParseUser.getCurrentUser().getList("isFollowing");
        if (following.size() > 0) {
            for (Object username : following) {
                if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", username);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(final List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                for (final ParseUser parseUser : objects) {
                                    ParseFile file = (ParseFile) parseUser.get("image");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null && data != null) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                profileusersf.add(bitmap);
                                                profilename.put(parseUser.getUsername(), parseUser.getString("name"));
                                                profileinfof.put(parseUser.getUsername(), bitmap);
                                                if (profileusersf.size() == following.size()) {
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                    initRecylerView();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        } else {
            Toast.makeText(getActivity(), "Follow some users first", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new UserFrag()).commit();
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void initRecylerView() {

        RecyclerView recyclerView = rootView.findViewById(R.id.recycelrViewf);
        RecyclerViewAdapterf adapter = new RecyclerViewAdapterf(getActivity(), following, followingf, profileinfof, profilename);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Following" + "</font>"));
        initName();
    }
}

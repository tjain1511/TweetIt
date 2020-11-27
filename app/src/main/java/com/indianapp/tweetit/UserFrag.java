package com.indianapp.tweetit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import java.util.List;

public class UserFrag extends Fragment {
    View rootView;
    RecyclerViewAdapteruser mAdapter;

    private static final String TAG = "MainActivity";

    private ArrayList<UserItem> mExampleList;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_user, container, false);
        return rootView;
    }

    private void filter(String text) {
        ArrayList<UserItem> filteredList = new ArrayList<>();
        for (UserItem item : mExampleList) {
            if (item.getText1().toLowerCase().contains(text.toLowerCase()) || item.getText3().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
    }


    private void initName() {
        editText = rootView.findViewById(R.id.edittext);
        mExampleList = new ArrayList<>();
        final ProgressBar progressbar = rootView.findViewById(R.id.progressBar);
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
                                    if (ParseUser.getCurrentUser().getList("isFollowing").contains(user.getUsername())) {
                                        mExampleList.add(new UserItem(bitmap, user.getUsername(), "Following", user.getString("name")));
                                    } else {
                                        mExampleList.add(new UserItem(bitmap, user.getUsername(), "Follow", user.getString("name")));
                                    }
                                    if (mExampleList.size() == objects.size()) {
                                        initRecylerView();
                                        progressbar.setVisibility(View.INVISIBLE);
                                        editText.setVisibility(View.VISIBLE);
                                    }

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void initRecylerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycelrViewuser);
        mAdapter = new RecyclerViewAdapteruser(getActivity(), mExampleList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Search" + "</font>"));

        initName();

        EditText editText = rootView.findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }
}

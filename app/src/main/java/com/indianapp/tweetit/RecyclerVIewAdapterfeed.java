package com.indianapp.tweetit;


import android.content.Context;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerVIewAdapterfeed extends RecyclerView.Adapter<RecyclerVIewAdapterfeed.ViewHolder> {

    private ArrayList<Integer> mLikesfeed = new ArrayList<>();
    private ArrayList<String> mUserNamesfeed = new ArrayList<>();
    private ArrayList<List<Object>> mLikedbyfeed = new ArrayList<>();
    private ArrayList<String> mDatefeed = new ArrayList<>();
    private ArrayList<String> mTimefeed = new ArrayList<>();
    private ArrayList<String> mImageTextfeed = new ArrayList<>();
    Bitmap bitmapprfeed;
    private Context mContextfeed;
    Map<String, Bitmap> mUsersImage = new HashMap<>();
    Map<String, String> mUsersName = new HashMap<>();


    public RecyclerVIewAdapterfeed(Context mContext, ArrayList<String> mImageTextfeed, ArrayList<String> mDatefeed, ArrayList<String> mTimefeed, ArrayList<Integer> mLikesfeed, ArrayList<List<Object>> mLikedbyfeed, Bitmap bitmapprfeed, ArrayList<String> mUserNamesfeed, Map<String, Bitmap> mUsersImage, Map<String, String> mUsersName) {
        this.mUsersImage = mUsersImage;
        this.bitmapprfeed = bitmapprfeed;
        this.mLikedbyfeed = mLikedbyfeed;
        this.mLikesfeed = mLikesfeed;
        this.mDatefeed = mDatefeed;
        this.mTimefeed = mTimefeed;
        this.mImageTextfeed = mImageTextfeed;
        this.mContextfeed = mContext;
        this.mUserNamesfeed = mUserNamesfeed;
        this.mUsersName = mUsersName;
    }

    public void userfeedredirect(String username, String name) {
        Fragment uff = new UserFeedFrag();
        Bundle args = new Bundle();
        args.putString("message", username);
        args.putString("activity", "following");
        args.putString("name", name);
        uff.setArguments(args);
        ((AppCompatActivity) mContextfeed).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, uff).commit();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedactivity, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.imageyourfeed.setImageBitmap(mUsersImage.get(mUserNamesfeed.get(position)));
        holder.yournamefeed.setText("@" + mUserNamesfeed.get(position));
        holder.dissname.setText(mUsersName.get(mUserNamesfeed.get(position).toString()).substring(0, 1).toUpperCase() + mUsersName.get(mUserNamesfeed.get(position).toString()).substring(1).toLowerCase());
        holder.imageTextfeed.setText(mImageTextfeed.get(position));
        holder.mTimefeed.setText(mTimefeed.get(position));
        holder.mDatefeed.setText(mDatefeed.get(position));
        holder.yournamefeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(mUserNamesfeed.get(position), mUsersName.get(mUserNamesfeed.get(position).toString()));
            }
        });
        holder.imageyourfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(mUserNamesfeed.get(position), mUsersName.get(mUserNamesfeed.get(position).toString()));
            }
        });

        holder.dissname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(mUserNamesfeed.get(position), mUsersName.get(mUserNamesfeed.get(position).toString()));
            }
        });

        if (mLikedbyfeed.get(position).size() == 1) {
            holder.mLikestfeed.setText(String.valueOf(mLikedbyfeed.get(position).size()) + " like");
        } else {
            holder.mLikestfeed.setText(String.valueOf(mLikedbyfeed.get(position).size()) + " likes");
        }

        if (mLikedbyfeed.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
            holder.likefeed.setImageResource(R.drawable.heart1);

        } else {
            holder.likefeed.setImageResource(R.drawable.heart);
        }

        holder.likefeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLikedbyfeed.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
                    holder.likefeed.setImageResource(R.drawable.heart);
                    mLikedbyfeed.get(position).remove(ParseUser.getCurrentUser().getUsername());
                    notifyDataSetChanged();
                    holder.likefeed.setClickable(false);

                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageTextfeed.get(position));
                    likedbyO.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject liks : objects) {

                                    liks.getList("likedby").remove(ParseUser.getCurrentUser().getUsername());
                                    List tem = liks.getList("likedby");
                                    liks.remove("likedby");
                                    liks.put("likedby", tem);

                                    liks.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                holder.likefeed.setClickable(true);
                                            } else {
                                                Toast.makeText(mContextfeed, "fucked", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                } else {
                    holder.likefeed.setImageResource(R.drawable.heart1);
                    mLikedbyfeed.get(position).add(ParseUser.getCurrentUser().getUsername());
                    notifyDataSetChanged();
                    holder.likefeed.setClickable(false);
                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageTextfeed.get(position));
                    likedbyO.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject liks : objects) {
                                    liks.add("likedby", ParseUser.getCurrentUser().getUsername());
                                    liks.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                holder.likefeed.setClickable(true);
                                            } else {
                                                Toast.makeText(mContextfeed, "fucked", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });


                }

            }
        });

    }

    @Override
    public int getItemCount() {

        return mImageTextfeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageyourfeed;
        ImageView deletefeed;
        TextView imageTextfeed;
        TextView yournamefeed;
        TextView mDatefeed;
        TextView mTimefeed;
        TextView mLikestfeed;
        ImageView likefeed;
        TextView dissname;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageyourfeed = itemView.findViewById(R.id.imagefeed);
            imageTextfeed = itemView.findViewById(R.id.image_namefeed);
            yournamefeed = itemView.findViewById(R.id.textView2feed);

            mDatefeed = itemView.findViewById(R.id.textView4feed);
            mTimefeed = itemView.findViewById(R.id.textView5feed);
            mLikestfeed = itemView.findViewById(R.id.textView3feed);
            likefeed = itemView.findViewById(R.id.imageView2feed);
            dissname = itemView.findViewById(R.id.diffname);
        }
    }
}

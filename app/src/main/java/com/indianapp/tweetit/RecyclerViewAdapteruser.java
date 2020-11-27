package com.indianapp.tweetit;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewAdapteruser extends RecyclerView.Adapter<RecyclerViewAdapteruser.ViewHolder> {


    private ArrayList<String> muser = new ArrayList<>();
    private ArrayList<String> mfollowing = new ArrayList<>();
    private Context mContext;
    ArrayList<UserItem> exampleList;
    String folooo;

    public void userfeedredirect(String username, String name) {
        Fragment uff = new UserFeedFrag();
        Bundle args = new Bundle();
        args.putString("message", username);
        args.putString("activity", "following");
        args.putString("name", name);
        uff.setArguments(args);
        ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, uff).commit();

    }

    public RecyclerViewAdapteruser(Context mContext, ArrayList<UserItem> exampleList) {
        this.exampleList = exampleList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_useractivity, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapteruser.ViewHolder holder, final int position) {
        final UserItem currentItem = exampleList.get(position);
        folooo = currentItem.getText2();
        holder.imageText.setText("@" + currentItem.getText1());
        holder.realname.setText(currentItem.getText3());
        holder.imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(currentItem.getText1(), currentItem.getText3());

            }
        });
        holder.realname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(currentItem.getText1(), currentItem.getText3());

            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(currentItem.getText1(), currentItem.getText3());

            }
        });
        holder.followuser.setText(folooo);
        holder.image.setImageBitmap(currentItem.getImageResource());
        if (folooo.equals("Follow")) {
            holder.followuser.setTextColor(Color.rgb(255, 255, 255));
            holder.usercard.setCardBackgroundColor(Color.rgb(29, 161, 242));
        } else {
            holder.followuser.setTextColor(Color.rgb(0, 0, 0));
            holder.usercard.setCardBackgroundColor(Color.rgb(255, 255, 255));
        }


        holder.followuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.followuser.getText().toString().equals("Follow")) {
                    holder.followuser.setText("Following");
                    holder.followuser.setTextColor(Color.rgb(0, 0, 0));
                    holder.usercard.setCardBackgroundColor(Color.rgb(255, 255, 255));

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", currentItem.getText1());
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {
                            object.add("followedBy", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                    } else {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                    });
                    ParseUser.getCurrentUser().add("isFollowing", currentItem.getText1());
                    folooo = "Following";
                    currentItem.setmText2("Following");


                } else {
                    holder.followuser.setText("Follow");
                    holder.followuser.setTextColor(Color.rgb(255, 255, 255));
                    holder.usercard.setCardBackgroundColor(Color.rgb(29, 161, 242));

                    ParseUser.getCurrentUser().getList("isFollowing").remove(currentItem.getText1());
                    List tem = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", tem);
                    folooo = "Follow";
                    currentItem.setmText2("Follow");
                }
                ParseUser.getCurrentUser().saveInBackground();

            }
        });
    }

    @Override
    public int getItemCount() {

        return exampleList.size();
    }

    public void filterList(ArrayList<UserItem> filteredList) {
        exampleList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView followuser;
        TextView imageText;
        TextView realname;
        CardView usercard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usercard = itemView.findViewById(R.id.usercard);
            followuser = itemView.findViewById(R.id.followuser);
            image = itemView.findViewById(R.id.image_user);
            imageText = itemView.findViewById(R.id.text);
            realname = itemView.findViewById(R.id.realname);


        }
    }
}

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

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterf extends RecyclerView.Adapter<RecyclerViewAdapterf.ViewHolder> {


    private List<Object> muserf = new ArrayList<>();
    private ArrayList<String> mfollowingf = new ArrayList<>();
    Map<String, Bitmap> mprofileUsersf = new HashMap<>();
    Map<String, String> mprofilename = new HashMap<>();
    private Context mContext;

    public void userfeedredirect(String username, String name) {
        Fragment uff = new UserFeedFrag();
        Bundle args = new Bundle();
        args.putString("message", username);
        args.putString("activity", "following");
        args.putString("name", name);
        uff.setArguments(args);
        ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, uff).commit();

    }

    public RecyclerViewAdapterf(Context mContext, List<Object> muserf, ArrayList<String> mfollowingf, Map<String, Bitmap> mprofileUsersf, Map<String, String> mprofilename) {
        this.mprofilename = mprofilename;
        this.mprofileUsersf = mprofileUsersf;
        this.mfollowingf = mfollowingf;
        this.muserf = muserf;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_following, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterf.ViewHolder holder, final int position) {
        holder.imageText.setText("@" + muserf.get(position).toString());
        holder.realnamef.setText(mprofilename.get(muserf.get(position).toString()).substring(0, 1).toUpperCase() + mprofilename.get(muserf.get(position).toString()).substring(1).toLowerCase());
        holder.imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(muserf.get(position).toString(), mprofilename.get(muserf.get(position).toString()));

            }
        });
        holder.realnamef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(muserf.get(position).toString(), mprofilename.get(muserf.get(position).toString()));

            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userfeedredirect(muserf.get(position).toString(), mprofilename.get(muserf.get(position).toString()));

            }
        });

        holder.image.setImageBitmap(mprofileUsersf.get(muserf.get(position).toString()));
        holder.followuser.setTextColor(Color.rgb(255, 255, 255));
        holder.usercard.setCardBackgroundColor(Color.rgb(29, 161, 242));
        for (Object username : muserf) {
            if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
                holder.followuser.setText("Following");
                holder.followuser.setTextColor(Color.rgb(0, 0, 0));
                holder.usercard.setCardBackgroundColor(Color.rgb(255, 255, 255));
            }
        }


        holder.followuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.followuser.getText().toString().equals("Follow")) {
                    holder.followuser.setTextColor(Color.rgb(255, 255, 255));
                    holder.usercard.setCardBackgroundColor(Color.rgb(29, 161, 242));
                    holder.followuser.setText("Following");
                    ParseUser.getCurrentUser().add("isFollowing", muserf.get(position));


                } else {
                    holder.followuser.setText("Follow");
                    holder.followuser.setTextColor(Color.rgb(0, 0, 0));
                    holder.usercard.setCardBackgroundColor(Color.rgb(255, 255, 255));
                    ParseUser.getCurrentUser().getList("isFollowing").remove(muserf.get(position));
                    List tem = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", tem);
                }
                notifyDataSetChanged();
                ParseUser.getCurrentUser().saveInBackground();
                notifyDataSetChanged();
                muserf = ParseUser.getCurrentUser().getList("isFollowing");
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {

        return muserf.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView followuser;
        TextView imageText;
        CardView usercard;
        TextView realnamef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            followuser = itemView.findViewById(R.id.followuserf);
            image = itemView.findViewById(R.id.imageuserf);
            imageText = itemView.findViewById(R.id.textf);
            usercard = itemView.findViewById(R.id.followcard);
            realnamef = itemView.findViewById(R.id.realnamef);
        }
    }
}

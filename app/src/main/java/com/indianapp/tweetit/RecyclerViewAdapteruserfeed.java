package com.indianapp.tweetit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewAdapteruserfeed extends RecyclerView.Adapter<RecyclerViewAdapteruserfeed.ViewHolder> {

    private ArrayList<Integer> mLikesuf = new ArrayList<>();
    private ArrayList<List<Object>> mLikedbyuf = new ArrayList<>();
    private ArrayList<String> mDateuf = new ArrayList<>();
    private ArrayList<String> mTimeuf = new ArrayList<>();
    private ArrayList<String> mImageTextuf = new ArrayList<>();
    private List<Object> isFollowinguf = new ArrayList<>();
    Bitmap bitmappruf;
    private Context mContextuf;
    String usernameuf;
    String nameuf;
    TextView ufl;


    public RecyclerViewAdapteruserfeed(Context mContextuf, ArrayList<String> mImageTextuf, ArrayList<String> mDateuf, ArrayList<String> mTimeuf, ArrayList<Integer> mLikesuf, ArrayList<List<Object>> mLikedbyuf, Bitmap bitmappruf, String usernameuf, List<Object> isFollowinguf, String nameuf, TextView ufl) {
        this.ufl = ufl;
        this.isFollowinguf = isFollowinguf;
        this.bitmappruf = bitmappruf;
        this.mLikedbyuf = mLikedbyuf;
        this.mLikesuf = mLikesuf;
        this.mDateuf = mDateuf;
        this.mTimeuf = mTimeuf;
        this.mImageTextuf = mImageTextuf;
        this.mContextuf = mContextuf;
        this.usernameuf = usernameuf;
        this.nameuf = nameuf;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_userfeed, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.imageyouruf.setImageBitmap(bitmappruf);
        holder.yournameuf.setText("@" + usernameuf);
        holder.nameufl.setText(nameuf.substring(0, 1).toUpperCase() + nameuf.substring(1).toLowerCase());
        holder.imageTextuf.setText(mImageTextuf.get(position));
        holder.mTimeuf.setText(mTimeuf.get(position));
        holder.mDateuf.setText(mDateuf.get(position));
        if (mLikedbyuf.get(position).size() == 1) {
            holder.mLikestuf.setText(String.valueOf(mLikedbyuf.get(position).size()) + " like");
        } else {
            holder.mLikestuf.setText(String.valueOf(mLikedbyuf.get(position).size()) + " likes");
        }
        if (mLikedbyuf.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
            holder.likeuf.setImageResource(R.drawable.heart1);

        } else {
            holder.likeuf.setImageResource(R.drawable.heart);
        }

        holder.likeuf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLikedbyuf.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
                    holder.likeuf.setImageResource(R.drawable.heart);
                    mLikedbyuf.get(position).remove(ParseUser.getCurrentUser().getUsername());
                    ufl.setText(String.valueOf(Integer.parseInt(ufl.getText().toString()) - 1));
                    notifyDataSetChanged();
                    holder.likeuf.setClickable(false);

                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageTextuf.get(position));
                    likedbyO.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject liks : objects) {
                                    //  liks.add("likedby", ParseUser.getCurrentUser().getUsername());

                                    liks.getList("likedby").remove(ParseUser.getCurrentUser().getUsername());
                                    List tem = liks.getList("likedby");
                                    liks.remove("likedby");
                                    liks.put("likedby", tem);

                                    liks.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                holder.likeuf.setClickable(true);
                                            } else {
                                                Toast.makeText(mContextuf, "fucked", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                } else {
                    holder.likeuf.setImageResource(R.drawable.heart1);
                    mLikedbyuf.get(position).add(ParseUser.getCurrentUser().getUsername());
                    ufl.setText(String.valueOf(Integer.parseInt(ufl.getText().toString()) + 1));
                    notifyDataSetChanged();
                    holder.likeuf.setClickable(false);
                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageTextuf.get(position));
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
                                                holder.likeuf.setClickable(true);
                                            } else {
                                                Toast.makeText(mContextuf, "fucked", Toast.LENGTH_SHORT).show();
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

        return mImageTextuf.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageyouruf;

        TextView imageTextuf;
        TextView yournameuf;
        TextView mDateuf;
        TextView mTimeuf;
        TextView mLikestuf;
        ImageView likeuf;
        TextView isFollowinguft;
        TextView nameufl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageyouruf = itemView.findViewById(R.id.image_useruf);
            imageTextuf = itemView.findViewById(R.id.image_nameuf);
            yournameuf = itemView.findViewById(R.id.textView2uf);
            mDateuf = itemView.findViewById(R.id.textView4uf);
            mTimeuf = itemView.findViewById(R.id.textView5uf);
            mLikestuf = itemView.findViewById(R.id.textView3uf);
            likeuf = itemView.findViewById(R.id.imageView2uf);
            isFollowinguft = itemView.findViewById(R.id.isFollowinguf);
            nameufl = itemView.findViewById(R.id.nameufl);
        }
    }
}

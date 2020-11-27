package com.indianapp.tweetit;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Integer> mLikes = new ArrayList<>();
    private ArrayList<List<Object>> mLikedby = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mImageText = new ArrayList<>();
    Bitmap bitmappr;
    private Context mContext;
    TextView postspr;
    String nametst;
    TextView likespr;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageText, ArrayList<String> mDate, ArrayList<String> mTime, ArrayList<Integer> mLikes, ArrayList<List<Object>> mLikedby, Bitmap bitmappr, String nametst, TextView likespr, TextView postspr) {
        this.postspr = postspr;
        this.likespr = likespr;
        this.bitmappr = bitmappr;
        this.mLikedby = mLikedby;
        this.mLikes = mLikes;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mImageText = mImageText;
        this.mContext = mContext;
        this.nametst = nametst;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.imageyour.setImageBitmap(bitmappr);
        holder.yourname.setText("@" + ParseUser.getCurrentUser().getUsername());
        holder.nameprl.setText(nametst);
        holder.imageText.setText(mImageText.get(position));
        holder.mTime.setText(mTime.get(position));
        holder.mDate.setText(mDate.get(position));
        holder.mLikest.setText(String.valueOf(mLikedby.get(position).size()));
        if (mLikedby.get(position).size() == 1) {
            holder.mLikest.setText(String.valueOf(mLikedby.get(position).size()) + " like");
        } else {
            holder.mLikest.setText(String.valueOf(mLikedby.get(position).size()) + " likes");
        }
        if (mLikedby.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
            holder.like.setImageResource(R.drawable.heart1);

        } else {
            holder.like.setImageResource(R.drawable.heart);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLikedby.get(position).contains(ParseUser.getCurrentUser().getUsername())) {
                    holder.like.setImageResource(R.drawable.heart);
                    mLikedby.get(position).remove(ParseUser.getCurrentUser().getUsername());
                    notifyDataSetChanged();
                    holder.like.setClickable(false);

                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageText.get(position));
                    likedbyO.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject liks : objects) {
                                    liks.getList("likedby").remove(ParseUser.getCurrentUser().getUsername());
                                    List tem = liks.getList("likedby");
                                    liks.remove("likedby");
                                    liks.put("likedby", tem);
                                    likespr.setText(String.valueOf(Integer.parseInt(likespr.getText().toString()) - 1));
                                    liks.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                holder.like.setClickable(true);
                                            } else {
                                                Toast.makeText(mContext, "fucked", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                } else {
                    holder.like.setImageResource(R.drawable.heart1);
                    mLikedby.get(position).add(ParseUser.getCurrentUser().getUsername());
                    likespr.setText(String.valueOf(Integer.parseInt(likespr.getText().toString()) + 1));
                    notifyDataSetChanged();
                    holder.like.setClickable(false);
                    final ParseQuery<ParseObject> likedbyO = ParseQuery.getQuery("Tweet");
                    likedbyO.whereEqualTo("tweet", mImageText.get(position));
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
                                                holder.like.setClickable(true);
                                            } else {
                                                Toast.makeText(mContext, "fucked", Toast.LENGTH_SHORT).show();
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


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Delete Tweet");
                alert.setMessage("Are you sure you want to delete this tweet?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.whereEqualTo("tweet", mImageText.get(position));

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject tweet : objects) {
                                        try {
                                            tweet.delete();

                                        } catch (ParseException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    mImageText.remove(position);
                                    notifyDataSetChanged();
                                    postspr.setText(String.valueOf(Integer.parseInt(postspr.getText().toString()) - 1));
                                    likespr.setText(String.valueOf(Integer.parseInt(likespr.getText().toString()) - mLikedby.get(position).size()));

                                }
                            }
                        });

                    }
                });
                alert.setIcon(R.drawable.ic_baseline_delete_forever_24);
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();


            }
        });

    }

    @Override
    public int getItemCount() {

        return mImageText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageyour;
        ImageView delete;
        TextView imageText;
        TextView yourname;
        TextView mDate;
        TextView mTime;
        TextView mLikest;
        ImageView like;
        TextView nameprl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageyour = itemView.findViewById(R.id.image_user);
            imageText = itemView.findViewById(R.id.image_name);
            yourname = itemView.findViewById(R.id.textView2);
            delete = itemView.findViewById(R.id.delete);
            mDate = itemView.findViewById(R.id.textView4);
            mTime = itemView.findViewById(R.id.textView5);
            mLikest = itemView.findViewById(R.id.textView3);
            like = itemView.findViewById(R.id.imageView2);
            nameprl = itemView.findViewById(R.id.nameprl);
        }
    }
}

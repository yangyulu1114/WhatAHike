package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.ui.activity.DetailedTrailActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;

    private PingWebServiceTask task;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView user;
        TextView text;
        TextView time;
        ImageView[] images;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;

            user = view.findViewById(R.id.comment_user_name);
            text = view.findViewById(R.id.comment_text);
            time = view.findViewById(R.id.post_time);

            images = new ImageView[6];
            images[0] = (ImageView) view.findViewById(R.id.image1);
            images[1] = (ImageView) view.findViewById(R.id.image2);
            images[2] = (ImageView) view.findViewById(R.id.image3);
            images[3] = (ImageView) view.findViewById(R.id.image4);
            images[4] = (ImageView) view.findViewById(R.id.image5);
            images[5] = (ImageView) view.findViewById(R.id.image6);
        }
    }
    public UserCommentAdapter(List<Comment> commentList) {
        task = new PingWebServiceTask();
        mCommentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {
        try {
            for (Comment comment : mCommentList) {
                holder.user.setText(comment.getUserId());
                holder.text.setText(comment.getText());
                holder.time.setText(new Date(comment.getTimeStamp()).toString());

                List<String> images = comment.getImages();
                for (int i = 0; i < images.size() && i < 6; i++) {
                    Drawable image = task.doInBackground(images.get(i));
                    holder.images[i].setBackground(image);
                    holder.images[i].setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e("comment adapter: ", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    private class PingWebServiceTask extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... strings) {

            Drawable drawable = null;
            try {
                InputStream iStream = (InputStream) new URL(strings[0]).getContent();
                drawable = Drawable.createFromStream(iStream, "image");
            } catch (Exception e) {
                Log.e("comment adapter: ", e.toString());
            }

            return drawable;
        }
    }
}

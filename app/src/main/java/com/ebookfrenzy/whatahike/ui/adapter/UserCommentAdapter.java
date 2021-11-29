package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcelable;
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
import com.ebookfrenzy.whatahike.ui.activity.AddCommentActivity;
import com.ebookfrenzy.whatahike.ui.activity.ImagePreviewActivity;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;
    private String mTrailId;

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

            images = new ImageView[9];
            images[0] = (ImageView) view.findViewById(R.id.image1);
            images[1] = (ImageView) view.findViewById(R.id.image2);
            images[2] = (ImageView) view.findViewById(R.id.image3);
            images[3] = (ImageView) view.findViewById(R.id.image4);
            images[4] = (ImageView) view.findViewById(R.id.image5);
            images[5] = (ImageView) view.findViewById(R.id.image6);
            images[6] = (ImageView) view.findViewById(R.id.image7);
            images[7] = (ImageView) view.findViewById(R.id.image8);
            images[8] = (ImageView) view.findViewById(R.id.image9);

        }
    }
    public UserCommentAdapter(List<Comment> commentList, String trailId) {
        task = new PingWebServiceTask();
        mCommentList = commentList;
        mTrailId = trailId;
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
                WebServiceTaskParams params = new WebServiceTaskParams(holder, images);
                task.execute(params);
            }
        } catch (Exception e) {
            Log.e("comment adapter: ", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    private class PingWebServiceTask extends AsyncTask<WebServiceTaskParams, Void, WebServiceTaskReturn> {

        @Override
        protected WebServiceTaskReturn doInBackground(WebServiceTaskParams... params) {
            ViewHolder holder = params[0].holder;
            List<String> images = params[0].images;

            List<Drawable> drawables = new ArrayList<>();
            try {
                for (int i = 0; i < images.size() && i < 9; i++) {
                    InputStream iStream = (InputStream) new URL(images.get(i)).getContent();
                    Drawable drawable = Drawable.createFromStream(iStream, "image");
                    drawables.add(drawable);

                    // set up listener
                    int index = i;
                    holder.images[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ImagePreviewActivity.class);
                            intent.putExtra("position", index);
                            intent.putStringArrayListExtra("imageList", (ArrayList<String>) images);
                            mContext.startActivity(intent);
                        }
                    });
                }

            } catch (Exception e) {
                Log.e("comment adapter task: ", e.toString());
            }

            WebServiceTaskReturn res = new WebServiceTaskReturn(holder, drawables);
            return res;
        }

        @Override
        protected void onPostExecute(WebServiceTaskReturn param) {
            super.onPostExecute(param);

            ViewHolder holder = param.holder;
            List<Drawable> images = param.images;

            for (int i = 0; i < images.size(); i++) {
                // set up the view
                holder.images[i].setBackground(images.get(i));
                holder.images[i].setVisibility(View.VISIBLE);
            }
        }
    }

    private static class WebServiceTaskParams {
        ViewHolder holder;
        List<String> images;
        WebServiceTaskParams(ViewHolder holder, List<String> images) {
            this.holder = holder;
            this.images = images;
        }
    }

    private static class WebServiceTaskReturn {
        ViewHolder holder;
        List<Drawable> images;
        WebServiceTaskReturn(ViewHolder holder, List<Drawable> images) {
            this.holder = holder;
            this.images = images;
        }
    }

}

package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.ui.activity.ImagePreviewActivity;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;
    private String mTrailId;

    static class ViewHolder extends RecyclerView.ViewHolder {
        int MAXLINES = 3;

        CardView cardView;
        TextView user;
        TextView text;
        TextView time;
        TextView expansionButton;

        boolean isExpansion;

        ImageView userIcon;
        ImageView[] images;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;

            user = view.findViewById(R.id.comment_user_name);
            text = view.findViewById(R.id.comment_text);
            expansionButton = view.findViewById(R.id.expansion_button);
            time = view.findViewById(R.id.post_time);

            userIcon = view.findViewById(R.id.userIcon);

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

        public void initTextView(String content) {
            if (content == null || content.length() == 0) {
                text.setVisibility(View.GONE);
                expansionButton.setVisibility(View.GONE);
                return;
            }

            setText(content);
            expansionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleExpansionStatus();
                }
            });
            expansionButton.setVisibility(View.GONE);
        }

        private void setText(String content) {
            text.setText(content);

            text.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = text.getLineCount();
                    if (lineCount > MAXLINES) {
                        expansionButton.setVisibility(View.VISIBLE);
                        expansionButton.setText("Read More");

                        text.setMaxLines(MAXLINES);
                        isExpansion = false;
                    } else {
                        expansionButton.setVisibility(View.GONE);
                    }
                }
            });

        }

        private void toggleExpansionStatus() {
            isExpansion = !isExpansion;
            if (isExpansion) {
                expansionButton.setText("Fold");
                text.setMaxLines(Integer.MAX_VALUE);
            } else {
                expansionButton.setText("Read More");
                text.setMaxLines(MAXLINES);
            }
        }
    }


    public UserCommentAdapter(List<Comment> commentList, String trailId) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = mCommentList.get(position);

        holder.setIsRecyclable(false);


        holder.user.setText(comment.getUserId());
        holder.time.setText(new Date(comment.getTimeStamp()).toString());

        // set up folded text
        holder.initTextView(comment.getText());

        // set up user icon

        // set up images
        List<String> images = comment.getImages();

        if (images == null)
            return;

        for (int i = 0; i < images.size() && i < 9; i++) {
            int index = i;
            ImageLoader.loadImage(images.get(i), new Listener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap data) {
                    // set image click listener
                    holder.images[index].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent previewIntent = new Intent(mContext, ImagePreviewActivity.class);
                            previewIntent.putStringArrayListExtra("imageList", (ArrayList<String>) images);
                            previewIntent.putExtra("position", index);
                            mContext.startActivity(previewIntent);
                        }
                    });
                    // set up the view
                    data = Bitmap.createScaledBitmap(data, 80, 80,true);
                    holder.images[index].setImageBitmap(data);
                    holder.images[index].setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailed(Exception e) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


}

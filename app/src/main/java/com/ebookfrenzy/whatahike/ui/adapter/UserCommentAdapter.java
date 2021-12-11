package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.ui.activity.ImagePreviewActivity;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;
    private String mTrailId;
    private Map<String, Drawable> iconMap;

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

            isExpansion = false;
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


    public UserCommentAdapter(List<Comment> commentList, Map<String, Drawable> iconMap, String trailId) {
        this.iconMap = iconMap;
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

        // set up user icon
        if (iconMap.get(comment.getUserId()) != null) {
            holder.userIcon.setImageDrawable(iconMap.get(comment.getUserId()));
        }

        holder.user.setText(comment.getUserId().split("@")[0]);
        holder.time.setText(calcTime(comment.getTimeStamp()));

        // set up folded text
        holder.initTextView(comment.getText());

        // set up images
        List<String> images = comment.getImages();

        if (images == null)
            return;

        for (int i = 0; i < 9; i++) {
            holder.images[i].setVisibility(i < images.size() ? View.VISIBLE : View.GONE);
        }

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
                    int length = data.getHeight() < data.getWidth() ? data.getHeight() : data.getWidth();
                    data = Bitmap.createBitmap(data, 0, 0, length, length);
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


    private String calcTime(long timestamp) {
        Date cur = new Date(System.currentTimeMillis());
        Date date = new Date(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int commentYear = calendar.get(Calendar.YEAR);
        int commentMonth = calendar.get(Calendar.MONTH);
        int commentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int commentHour = calendar.get(Calendar.HOUR);
        int commentMin = calendar.get(Calendar.MINUTE);
        calendar.setTime(cur);
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curHour = calendar.get(Calendar.HOUR);
        int curMin = calendar.get(Calendar.MINUTE);

        String res;
        if (commentYear != curYear || commentMonth != curMonth) {
            res = new java.sql.Date(timestamp).toString();
        } else if (commentDay != curDay) {
            int day = curDay - commentDay;
            res = day > 1 ? day + " Days Ago" : day + " Day Ago";
        } else if (commentHour != curHour) {
            int hour = curHour - commentHour;
            res = hour > 1 ? hour + " Hours Ago" : hour + " Hour Ago";
        } else if (commentMin != curMin) {
            int min = curMin - commentMin;
            res = min > 1 ? min + " Minutes Ago" : min + " Minute Ago";
        } else {
            res = "Just Now";
        }

        return res;
    }
}

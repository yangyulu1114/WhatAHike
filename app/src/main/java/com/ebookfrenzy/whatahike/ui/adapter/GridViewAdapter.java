package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.ui.view.SquareImageView;
import com.ebookfrenzy.whatahike.utils.DisplayUtil;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<String> mImageList = new ArrayList<>();
    private final int mPadding;

    public GridViewAdapter() {
        int width = DisplayUtil.getScreenSize()[0];
        mPadding = width / 10;
        Log.v("bush","mPadding " + mPadding );
    }

    public void setImageList(List<String> imageList) {
        mImageList.clear();
        mImageList.addAll(imageList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        Log.v("bush", "count " + mImageList.size());
        return mImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            Log.v("bush", "createView");
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, null);
            holder = new ViewHolder();
            holder.imageView = (SquareImageView) convertView;
            convertView.setTag(holder);
        } else {
            Log.v("bush", "reuseView");
            holder = (ViewHolder) convertView.getTag();
        }

        final ImageView imageView = holder.imageView;
        final String url = mImageList.get(position);
        holder.url=url;

        Log.v("bush", "position " + position + " url " + mImageList.get(position));
        if (mImageList.get(position) == "add") {
            imageView.setImageResource(R.drawable.add_image);
            imageView.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            ImageLoader.loadImage(url, new Listener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap data) {
                    Log.v("bush", "loadImage onSuccess");
                    ViewHolder holder = (ViewHolder) imageView.getTag();
                    if (holder.url == url) {
                        imageView.setImageBitmap(data);
                    }
                }
                @Override
                public void onFailed(Exception e) {
                    Log.e("bush", e.getMessage(), e);
                }
            });
            holder.imageView.setPadding(0,0,0,0);
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return convertView;
    }

    private class ViewHolder {
        SquareImageView imageView;
        String url;
    }
}

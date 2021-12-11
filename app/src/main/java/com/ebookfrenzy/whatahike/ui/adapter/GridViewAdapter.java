package com.ebookfrenzy.whatahike.ui.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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
    }

    public void setImageList(List<String> imageList) {
        mImageList.clear();
        mImageList.addAll(imageList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, null);
            holder = new ViewHolder();
            holder.imageView = (SquareImageView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ImageView imageView = holder.imageView;
        final String url = mImageList.get(position);
        holder.url=url;

        if (mImageList.get(position) == "add") {
            imageView.setImageResource(R.drawable.add_image);
            imageView.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            ImageLoader.loadImage(url, new Listener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap data) {
                    ViewHolder holder = (ViewHolder) imageView.getTag();
                    if (holder.url == url) {
                        imageView.setImageBitmap(data);
                    }
                }
                @Override
                public void onFailed(Exception e) {
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

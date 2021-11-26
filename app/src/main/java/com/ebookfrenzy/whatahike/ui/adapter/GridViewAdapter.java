package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.ebookfrenzy.whatahike.utils.DisplayUtil;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private final List<String> mImageList;
    private final int mSize;

    public GridViewAdapter(List<String> imageList) {
        mImageList = imageList;
        int width = DisplayUtil.getScreenSize()[0];
        Context context = MyApplication.getAppContext();
        int grid_margin = context.getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        int grid_space = context.getResources().getDimensionPixelOffset(R.dimen.grid_space);
        mSize = (width - grid_margin * 2 - grid_space * 2) / 3;
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
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.grid_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GridView.LayoutParams params = (GridView.LayoutParams) convertView.getLayoutParams();
        if (params == null) {
            params = new GridView.LayoutParams(mSize, mSize);
        }
        params.width = mSize;
        params.height = mSize;
        convertView.setLayoutParams(params);
        if (mImageList.get(position) == "add") {
            holder.imageView.setImageResource(R.drawable.add_image);
        } else {
            holder.imageView.setImageURI(Uri.parse(mImageList.get(position)));
        }

        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
        int size = (int) (mSize * 0.3);
        if (imageParams == null) {
            imageParams = new RelativeLayout.LayoutParams(size, size);
        }
        imageParams.width = size;
        imageParams.height = size;
        holder.imageView.setLayoutParams(imageParams);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}

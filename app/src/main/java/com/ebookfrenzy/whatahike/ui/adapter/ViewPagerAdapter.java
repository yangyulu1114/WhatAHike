package com.ebookfrenzy.whatahike.ui.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends PagerAdapter {
    private List<String> mImageList = new ArrayList<>();
    Map<Integer, View> map = new HashMap<>();

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
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewHolder holder = null;
        View view = null;
        if (!map.containsKey(position)) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.pager_item, null);
            map.put(position, view);
            holder = new ViewHolder();
            holder.imageView= view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            view = map.get(position);
            holder = (ViewHolder) view.getTag();
        }
        final ImageView imageView = holder.imageView;
        ImageLoader.loadImage(mImageList.get(position), new Listener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                imageView.setImageBitmap(data);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
        map.remove(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}

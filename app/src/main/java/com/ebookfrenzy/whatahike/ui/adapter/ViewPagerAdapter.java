package com.ebookfrenzy.whatahike.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ebookfrenzy.whatahike.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends PagerAdapter {
    private final List<String> mImageList;
    Map<Integer, View> map = new HashMap<>();

    public ViewPagerAdapter(List<String> imageList) {
        mImageList = imageList;
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
            holder.textView = view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            view = map.get(position);
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(mImageList.get(position));
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
        TextView textView;
    }
}

package com.ebookfrenzy.whatahike.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.trailRecord;

import java.util.ArrayList;

public class RviewAdapter extends RecyclerView.Adapter<RviewHolder>{
    private final ArrayList<trailRecord> itemList;

    public RviewAdapter(ArrayList<trailRecord> itemList) {
        this.itemList = itemList;
    }


    @Override
    public RviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_layout, parent, false);
        return new RviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RviewHolder holder, int position) {
        trailRecord currentItem = itemList.get(position);

        holder.picture.setImageResource(currentItem.getImage());
        holder.description.setText(currentItem.getInformation());
        holder.trailName.setText(currentItem.getInformation());
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

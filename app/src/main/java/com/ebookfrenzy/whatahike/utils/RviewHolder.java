package com.ebookfrenzy.whatahike.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;

public class RviewHolder extends RecyclerView.ViewHolder{
    public ImageView picture;
    public TextView description;
    public TextView trailName;

    public RviewHolder(View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.trailPicture);
        trailName = itemView.findViewById(R.id.trailName);
        description = itemView.findViewById(R.id.description);

    }
}

package com.ebookfrenzy.whatahike.ui.adapter;

import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.activity.MainActivity;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<Trail> trailList;
    private RecyclerViewClickListener listener;
    private ImageView trailImg;

    public RecyclerAdapter(List<Trail> trailList, RecyclerViewClickListener listener){
        this.trailList = trailList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTxt;
        private TextView areaTxt;
        private RatingBar difficultyRating;
        private TextView difficultyTxt;
        private TextView distanceTxt;
        private  ImageView trailImg;


        public MyViewHolder(final View view){
            super(view);
            nameTxt = view.findViewById(R.id.Name);
            areaTxt = view.findViewById(R.id.Area);
            difficultyRating = view.findViewById(R.id.ratingBar);
            difficultyTxt = view.findViewById(R.id.Difficulty);
            distanceTxt = view.findViewById(R.id.Distance);
            trailImg = view.findViewById(R.id.TrailImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Trail trail = trailList.get(position);
        holder.trailImg.setImageResource(R.drawable.ic_banner);
        ImageLoader.loadImage(trail.getIconURL(), new Listener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                holder.trailImg.setImageBitmap(data);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        String name = trail.getName();

        String area = trail.getArea();
        float difficulty =  trail.getDifficulty();
        difficulty = (float) Math.ceil(difficulty/2);
        Location location = MainActivity.getLocation();
        if (location == null) {
            holder.distanceTxt.setText("Can't fetch your location");
        }
        else {
            double distance = Math.round(RestAPI.getDistance(location.getLatitude(), location.getLongitude(), trail.getLocation()[0], trail.getLocation()[1]));
            holder.distanceTxt.setText("Distance: " + distance + " miles");
        }


        if(name.length() >= 30){
            name = name.substring(0, 30);
        }
        if(area.length() >= 28){
            area = area.substring(0, 28);
        }
        holder.nameTxt.setText(name);
        holder.areaTxt.setText("Area: " + area);
        holder.difficultyTxt.setText("Difficulty: ");
        holder.difficultyRating.setRating(difficulty);
    }

    @Override
    public int getItemCount() {
        if(trailList == null){
            return 0;
        }
        return trailList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}
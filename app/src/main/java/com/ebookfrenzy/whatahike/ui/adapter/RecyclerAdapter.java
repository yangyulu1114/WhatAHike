package com.ebookfrenzy.whatahike.ui.adapter;

import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.activity.MainActivity;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<Trail> trailList;
    private RecyclerViewClickListener listener;
    private ImageView trailImg;
    //private List<Trail> trailListFull;

    public RecyclerAdapter(List<Trail> trailList, RecyclerViewClickListener listener){
        this.trailList = trailList;
        this.listener = listener;
        // trailListFull = new ArrayList<>(trailList); //create the copy oif trail list
    }

  //  @Override
//    public Filter getFilter() {
//        return exampleFilter;
//    }
//
//    private Filter exampleFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<Trail> filteredList = new ArrayList<>();
//            if (constraint == null || constraint.length() == 0) {
//                filteredList.addAll(trailListFull);
//            }
//            else{
//                String filterPattern = constraint.toString().toLowerCase().trim();
//                for (Trail item : trailListFull) {
//                    if(item.getName().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(item);
//                    }
//                }
//            }
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//            return results;
//        }

//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            trailList.clear();
//            trailList.addAll((List)results.values);
//            notifyDataSetChanged();
//        }
//    };

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTxt;
        private TextView areaTxt;
        private TextView difficultyTxt;
        private TextView distanceTxt;


        public MyViewHolder(final View view){
            super(view);
            nameTxt = view.findViewById(R.id.Name);
            areaTxt = view.findViewById(R.id.Area);
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
        String name = trail.getName();
        String area = trail.getArea();
        String difficulty = String.valueOf(trail.getDifficulty());
        Location location = MainActivity.getLocation();
        double distance = Math.round(RestAPI.getDistance(location.getLatitude(), location.getLongitude(), trail.getLocation()[0], trail.getLocation()[1]));
        ImageLoader.loadImage(trail.getBannerURL(), new Listener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                trailImg.setImageBitmap(data);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        holder.nameTxt.setText(name);
        holder.areaTxt.setText("Area: " + area);
        holder.difficultyTxt.setText("Difficulty: " + difficulty);
        holder.distanceTxt.setText("Distance: " + distance);
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
package com.ebookfrenzy.whatahike.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.trailRecord;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements
        Filterable {
    private ArrayList<trailRecord> trailList;
    private ArrayList<trailRecord> trailListFull;

    public RecyclerAdapter(ArrayList<trailRecord> trailList){
        this.trailList = trailList;
        trailListFull = new ArrayList<>(trailList); //create the copy oif trail list
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<trailRecord> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(trailListFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (trailRecord item : trailListFull) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            trailList.clear();
            trailList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt;
        private TextView descriptionTxt;
        private TextView idTxt;
        private TextView difficultyTxt;

        public MyViewHolder(final View view){
            super(view);
            nameTxt = view.findViewById(R.id.trailName);
            descriptionTxt = view.findViewById(R.id.trailDescription);
            idTxt = view.findViewById(R.id.id);
            difficultyTxt = view.findViewById(R.id.difficulty);
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
        String desc = trailList.get(position).getDescription();
        String name = trailList.get(position).getName();
        String trailId = trailList.get(position).getId();
        String diff = String.valueOf(trailList.get(position).getDifficulty());


        holder.idTxt.setText("Trail Id " + trailId);
        holder.nameTxt.setText("Trail Name: " + name);
        holder.difficultyTxt.setText("Difficulty: " + diff);
        holder.descriptionTxt.setText( desc);
    }

    @Override
    public int getItemCount() {
        return trailList.size();
    }
}
package com.example.climacool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AdapterSubmenu extends RecyclerView.Adapter<AdapterSubmenu.Viewholder> {

    private Context context;
    private ArrayList<Model> modelArrayList;

    // Constructor
    public AdapterSubmenu(Context context, ArrayList<Model> modelList) {
        this.context = context;
        this.modelArrayList = modelList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_systems, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Model model = modelArrayList.get(position);
        holder.name.setText(model.getName());
        holder.image.setImageResource(model.getImage());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return modelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardSystemImage);
            name = itemView.findViewById(R.id.cardSystemName);
        }
    }



}
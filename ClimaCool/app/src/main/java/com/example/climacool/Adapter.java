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

public class Adapter extends RecyclerView.Adapter<Adapter.Viewholder> {

    private Context context;
    private ArrayList<Model> modelArrayList;

    // Constructor
    public Adapter(Context context, ArrayList<Model> courseModelArrayList) {
        this.context = context;
        this.modelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public Adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Model model = modelArrayList.get(position);
        holder.nameTV.setText(model.getName());
        holder.imageIV.setImageResource(model.getImage());
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
        private ImageView imageIV;
        private TextView nameTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageIV = itemView.findViewById(R.id.idIVCourseImage);
            nameTV = itemView.findViewById(R.id.idTVCourseName);
        }
    }



}
package com.example.zaikafoodyville;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView restaurantName,description,rating;
    View view;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.restaurantLogo);
        restaurantName=itemView.findViewById(R.id.restaurantName);
        description=itemView.findViewById(R.id.restDescription);
        rating=itemView.findViewById(R.id.rating);
        view=itemView;

    }
}

package com.example.eatelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter2 extends RecyclerView.Adapter<RestaurantAdapter2.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;

    public RestaurantAdapter2(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant2, parent, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());
        holder.eloTextView.setText(String.valueOf(restaurant.getElo()));
        holder.addressTextView.setText(restaurant.getAddress());
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView eloTextView;
        public TextView addressTextView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.restaurantName);
            eloTextView = itemView.findViewById(R.id.restaurantELO);
            addressTextView = itemView.findViewById(R.id.restaurantAddress);
        }
    }
}

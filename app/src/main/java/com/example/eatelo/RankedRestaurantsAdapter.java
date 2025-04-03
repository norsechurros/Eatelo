package com.example.eatelo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class RankedRestaurantsAdapter extends RecyclerView.Adapter<RankedRestaurantsAdapter.ViewHolder> {

    private List<String> rankedRestaurants;
    private OnStartDragListener dragListener;
    private Context context; // To show the alert dialog

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public RankedRestaurantsAdapter(Context context, List<String> rankedRestaurants, OnStartDragListener dragListener) {
        this.context = context;
        this.rankedRestaurants = rankedRestaurants;
        this.dragListener = dragListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ranked, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rankTextView.setText(rankedRestaurants.get(position));

        // Handle drag functionality
        holder.dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        });

        // Handle item click for removal
        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Restaurant")
                    .setMessage("Do you want to remove " + rankedRestaurants.get(position) + " from your rankings?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        rankedRestaurants.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, rankedRestaurants.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return rankedRestaurants.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(rankedRestaurants, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        ImageView dragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            dragHandle = itemView.findViewById(R.id.dragHandle);
        }
    }
}

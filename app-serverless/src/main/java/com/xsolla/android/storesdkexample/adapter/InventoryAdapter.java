package com.xsolla.android.storesdkexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xsolla.android.serverlessexample.R;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.listener.ConsumeListener;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Store.InventoryItem> items;
    private ConsumeListener consumeListener;

    public InventoryAdapter(List<Store.InventoryItem> items, ConsumeListener consumeListener) {
        this.items = items;
        this.consumeListener = consumeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemIcon;
        TextView itemName;
        TextView itemExpiration;
        Button consumeButton;
        TextView quantityLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemExpiration = itemView.findViewById(R.id.item_expiration);
            consumeButton = itemView.findViewById(R.id.consume_button);
            quantityLabel = itemView.findViewById(R.id.quantity_label);
        }

        public void bind(Store.InventoryItem item) {

            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());

            itemExpiration.setVisibility(View.GONE);

            if (!item.isConsumable()) {
                quantityLabel.setVisibility(View.VISIBLE);
                consumeButton.setVisibility(View.GONE);
            } else {
                quantityLabel.setVisibility(View.VISIBLE);
                consumeButton.setVisibility(View.VISIBLE);
            }

            quantityLabel.setText(String.valueOf(item.getQuantity()));
            consumeButton.setOnClickListener(v -> {
            });
        }
    }

}

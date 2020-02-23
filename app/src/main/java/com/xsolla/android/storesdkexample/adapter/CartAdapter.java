package com.xsolla.android.storesdkexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.listener.UpdateCartListener;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartResponse.Item> items;
    private UpdateCartListener updateCartListener;

    public CartAdapter(List<CartResponse.Item> items, UpdateCartListener updateCartListener) {
        this.items = items;
        this.updateCartListener = updateCartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemIcon;
        TextView itemName;
        TextView itemPrice;
        ImageView minusButton;
        ImageView addButton;
        TextView quantityLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            minusButton = itemView.findViewById(R.id.minus_button);
            addButton = itemView.findViewById(R.id.add_button);
            quantityLabel = itemView.findViewById(R.id.quantity_label);
        }

        public void bind(final int position) {
            final CartResponse.Item item = items.get(position);

            String itemSku = item.getSku();
            int quantity = item.getQuantity();

            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());

            String price = item.getPrice().getAmount();
            String currency = item.getPrice().getCurrency();
            String formattedPrice = price.substring(0, price.indexOf(".") + 3) + " " + currency;

            itemPrice.setText(formattedPrice);
            quantityLabel.setText(String.valueOf(quantity));

            minusButton.setOnClickListener(new ChangeQuantityListener(itemSku, quantity - 1));
            addButton.setOnClickListener(new ChangeQuantityListener(itemSku, quantity + 1));

            if (quantity == 1) {
                minusButton.setImageResource(R.drawable.ic_delete_24dp);
            } else {
                minusButton.setImageResource(R.drawable.ic_remove_circle_24dp);
            }
        }

        private void updateCart() {
            XStore.getCurrentCart(new XStoreCallback<CartResponse>() {
                @Override
                protected void onSuccess(CartResponse response) {
                    items = response.getItems();
                    if (!items.isEmpty()) {
                        updateCartListener.onCartUpdated(response.getPrice().getPrettyPrintAmount());
                        notifyDataSetChanged();
                    } else {
                        updateCartListener.onCartEmpty();
                    }
                }

                @Override
                protected void onFailure(String errorMessage) {
                    Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private class ChangeQuantityListener implements View.OnClickListener {
            private String itemSku;
            private int newQuantity;

            ChangeQuantityListener(String itemSku, int newQuantity) {
                this.itemSku = itemSku;
                this.newQuantity = newQuantity;
            }

            @Override
            public void onClick(View v) {
                XStore.updateItemFromCurrentCart(itemSku, newQuantity, new XStoreCallback<Void>() {
                    @Override
                    protected void onSuccess(Void response) {
                        updateCart();
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

}

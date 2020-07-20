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
import com.xsolla.android.store.entity.response.common.ExpirationPeriod;
import com.xsolla.android.store.entity.response.common.IPrice;
import com.xsolla.android.store.entity.response.common.InventoryOption;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.listener.UpdateCartListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import java.math.RoundingMode;
import java.util.List;

public class OldCartAdapter extends RecyclerView.Adapter<OldCartAdapter.ViewHolder> {

    private List<CartResponse.Item> items;
    private UpdateCartListener updateCartListener;

    public OldCartAdapter(List<CartResponse.Item> items, UpdateCartListener updateCartListener) {
        this.items = items;
        this.updateCartListener = updateCartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_old, parent, false);
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
        TextView itemExpiration;
        ImageView minusButton;
        ImageView addButton;
        TextView quantityLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemExpiration = itemView.findViewById(R.id.item_expiration);
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

            InventoryOption inventoryOption = item.getInventoryOption();
            ExpirationPeriod expirationPeriod = null;
            if (inventoryOption != null) {
                expirationPeriod = inventoryOption.getExpirationPeriod();
            }
            if (expirationPeriod == null) {
                itemExpiration.setVisibility(View.GONE);
            } else {
                itemExpiration.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                sb.append("Expiration: ");
                sb.append(expirationPeriod.getValue());
                sb.append(' ');
                sb.append(expirationPeriod.getType().name().toLowerCase());
                if (expirationPeriod.getValue() != 1) {
                    sb.append('s');
                }
                itemExpiration.setText(sb);
            }

            IPrice price = item.getPrice();
            String formattedPrice = price.getAmountDecimal().setScale(2, RoundingMode.HALF_UP) + " " + price.getCurrencyName();

            itemPrice.setText(formattedPrice);
            quantityLabel.setText(String.valueOf(quantity));

            minusButton.setOnClickListener(new ChangeQuantityListener(itemSku, quantity - 1));
            addButton.setOnClickListener(new ChangeQuantityListener(itemSku, quantity + 1));

            if (quantity == 1) {
                minusButton.setImageResource(R.drawable.ic_delete_item_button);
            } else {
                minusButton.setImageResource(R.drawable.ic_remove_item_button);
            }
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
                ViewUtils.disable(v);
                XStore.updateItemFromCurrentCart(itemSku, newQuantity, new XStoreCallback<Void>() {
                    @Override
                    protected void onSuccess(Void response) {
                        updateCart();
                        ViewUtils.enable(v);
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        ViewUtils.enable(v);
                    }
                });
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
    }
}

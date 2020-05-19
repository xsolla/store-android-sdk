package com.xsolla.android.storesdkexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.listener.AddToCartListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhysicalItemsAdapter extends RecyclerView.Adapter<PhysicalItemsAdapter.ViewHolder> {

    private List<PhysicalItemsResponse.Item> items;
    private AddToCartListener addToCartListener;

    public PhysicalItemsAdapter(List<PhysicalItemsResponse.Item> items, AddToCartListener listener) {
        this.items = items;
        this.addToCartListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
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
        TextView itemPrice;
        ImageView addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            addToCartButton = itemView.findViewById(R.id.buy_button);
        }

        private void bind(final PhysicalItemsResponse.Item item) {
            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());
            itemPrice.setText(item.getPrice().getPrettyPrintAmount());

            addToCartButton.setOnClickListener(v -> {
                ViewUtils.disable(v);
                XStore.getCurrentCart(new XStoreCallback<CartResponse>() {
                    @Override
                    protected void onSuccess(CartResponse response) {
                        int quantity = 1;

                        for (CartResponse.Item cartItem: response.getItems()) {
                            if (cartItem.getSku().equals(item.getSku())) {
                                quantity += cartItem.getQuantity();
                            }
                        }

                        XStore.updateItemFromCurrentCart(item.getSku(), quantity, new XStoreCallback<Void>() {
                            @Override
                            protected void onSuccess(Void response) {
                                addToCartListener.onSuccess();
                                ViewUtils.enable(v);
                            }

                            @Override
                            protected void onFailure(String errorMessage) {
                                addToCartListener.onFailure(errorMessage);
                                ViewUtils.enable(v);
                            }
                        });
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        addToCartListener.onFailure(errorMessage);
                        ViewUtils.enable(v);
                    }
                });
            });
        }

    }

}

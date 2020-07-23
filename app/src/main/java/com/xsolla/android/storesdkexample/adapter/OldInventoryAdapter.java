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
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.inventory.InventoryResponse;
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.listener.ConsumeListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OldInventoryAdapter extends RecyclerView.Adapter<OldInventoryAdapter.ViewHolder> {

    private List<InventoryResponse.Item> items;
    private List<SubscriptionsResponse.Item> subscriptions;
    private ConsumeListener consumeListener;

    public OldInventoryAdapter(List<InventoryResponse.Item> items, ConsumeListener consumeListener) {
        this.items = items;
        this.consumeListener = consumeListener;
    }

    public void setSubscriptions(List<SubscriptionsResponse.Item> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_old, parent, false);
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

        public void bind(InventoryResponse.Item item) {

            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());

            if (item.getVirtualItemType() == InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) {
                itemExpiration.setText(getExpirationText(item));
                itemExpiration.setVisibility(View.VISIBLE);
            } else {
                itemExpiration.setVisibility(View.GONE);
            }

            if (item.getRemainingUses() == 0) {
                quantityLabel.setVisibility(View.GONE);
                consumeButton.setVisibility(View.GONE);
                return;
            } else {
                quantityLabel.setVisibility(View.VISIBLE);
                consumeButton.setVisibility(View.VISIBLE);
            }

            quantityLabel.setText(String.valueOf(item.getQuantity()));
            consumeButton.setOnClickListener(v -> {
                ViewUtils.disable(v);

                XStore.consumeItem(item.getSku(), 1, null, new XStoreCallback<Void>() {
                    @Override
                    protected void onSuccess(Void response) {
                        consumeListener.onSuccess();
                        XStore.getInventory(new XStoreCallback<InventoryResponse>() {
                            @Override
                            protected void onSuccess(InventoryResponse response) {
                                List<InventoryResponse.Item> virtualItems = new ArrayList<>();
                                for (InventoryResponse.Item item : response.getItems()) {
                                    if (item.getType() == InventoryResponse.Item.Type.VIRTUAL_GOOD) {
                                        virtualItems.add(item);
                                    }
                                }

                                items = virtualItems;
                                notifyDataSetChanged();
                                ViewUtils.enable(v);
                            }

                            @Override
                            protected void onFailure(String errorMessage) {
                                consumeListener.onFailure(errorMessage);
                                ViewUtils.enable(v);
                            }
                        });
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        consumeListener.onFailure(errorMessage);
                        ViewUtils.enable(v);
                    }
                });
            });
        }
    }

    private String getExpirationText(InventoryResponse.Item item) {
        if (subscriptions == null) {
            return null;
        }
        if (item.getVirtualItemType() != InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) {
            return null;
        }
        for (SubscriptionsResponse.Item subscription : subscriptions) {
            if (subscription.getSku().equals(item.getSku())) {
                if (subscription.getStatus() == SubscriptionsResponse.Item.Status.ACTIVE) {
                    Date date = new java.util.Date(subscription.getExpiredAt()*1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                    String formattedDate = sdf.format(date);
                    return "Active until: " + formattedDate;
                } else {
                    return "Expired";
                }
            }
        }
        return null;
    }

}

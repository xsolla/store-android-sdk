package com.xsolla.android.storesdkexample.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xsolla.android.simplifiedexample.R;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.listener.CreatePaystationIntentListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.util.List;

public class VirtualCurrencyAdapter extends RecyclerView.Adapter<VirtualCurrencyAdapter.ViewHolder> {

    private Context context;
    private List<Store.VirtualCurrencyPack> items;
    private CreatePaystationIntentListener createPaystationIntentListener;

    public VirtualCurrencyAdapter(
            Context context,
            List<Store.VirtualCurrencyPack> virtualCurrencyPacks,
            CreatePaystationIntentListener createPaystationIntentListener
    ) {
        this.context = context;
        this.items = virtualCurrencyPacks;
        this.createPaystationIntentListener = createPaystationIntentListener;
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
        ImageView buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            buyButton = itemView.findViewById(R.id.buy_button);
        }

        private void bind(final Store.VirtualCurrencyPack item) {
            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());

            List<Store.Price> realPrices = item.getRealPrices();
            List<Store.Price> virtualPrices = item.getVirtualPrices();

            if (virtualPrices != null && !virtualPrices.isEmpty()) {
                String priceText = virtualPrices.get(0).getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + virtualPrices.get(0).getCurrencyName();
                itemPrice.setText(priceText);
                buyButton.setImageResource(R.drawable.ic_buy_button);
                initForVirtualCurrency(item);
                return;
            }

            if (realPrices != null && !realPrices.isEmpty()) {
                String priceText = realPrices.get(0).getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + realPrices.get(0).getCurrencyName();
                itemPrice.setText(priceText);
                buyButton.setImageResource(R.drawable.ic_buy_button);
                initForRealCurrency(item);
            }
        }

        private void initForRealCurrency(Store.VirtualCurrencyPack item) {
            buyButton.setOnClickListener(v -> {
                ViewUtils.disable(v);
                Store.createPaystationIntent(context, item.getSku(), new Store.CreatePaystationIntentCallback() {
                    @Override
                    public void onSuccess(@NotNull Intent intent, @NotNull String externalId) {
                        createPaystationIntentListener.onIntentCreated(intent, externalId);
                        ViewUtils.enable(v);
                    }

                    @Override
                    public void onFailure(@NotNull String errorMessage) {
                        createPaystationIntentListener.onFailure(errorMessage);
                        ViewUtils.enable(v);
                    }
                });
            });
        }

        private void initForVirtualCurrency(Store.VirtualCurrencyPack item) {
            Store.Price price = item.getVirtualPrices().get(0);
            buyButton.setOnClickListener(v -> {
            });
        }

    }

}

package com.xsolla.android.storesdkexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xsolla.android.payments.playfab.XPlayfabWrapper;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.listener.BuyForVirtualCurrencyListener;
import com.xsolla.android.storesdkexample.listener.CreateOrderListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VirtualCurrencyAdapter extends RecyclerView.Adapter<VirtualCurrencyAdapter.ViewHolder> {

    private List<Store.VirtualCurrencyPack> items;
    private CreateOrderListener createOrderListener;
    private BuyForVirtualCurrencyListener buyForVirtualCurrencyListener;

    public VirtualCurrencyAdapter(
            List<Store.VirtualCurrencyPack> virtualCurrencyPacks,
            CreateOrderListener createOrderListener,
            BuyForVirtualCurrencyListener buyForVirtualCurrencyListener
    ) {
        this.items = virtualCurrencyPacks;
        this.createOrderListener = createOrderListener;
        this.buyForVirtualCurrencyListener = buyForVirtualCurrencyListener;
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
                String priceText = virtualPrices.get(0).getAmount().toPlainString() + " " + virtualPrices.get(0).getCurrencyName();
                itemPrice.setText(priceText);
                buyButton.setImageResource(R.drawable.ic_buy_button);
                initForVirtualCurrency(item);
                return;
            }

            if (realPrices != null && !realPrices.isEmpty()) {
                String priceText = realPrices.get(0).getAmount().toPlainString() + " " + realPrices.get(0).getCurrencyName();
                itemPrice.setText(priceText);
                buyButton.setImageResource(R.drawable.ic_buy_button);
                initForRealCurrency(item);
            }
        }

        private void initForRealCurrency(Store.VirtualCurrencyPack item) {
            buyButton.setOnClickListener(v -> {
                ViewUtils.disable(v);
                XPlayfabWrapper.createPlayfabOrder(
                        item.getSku(),
                        1,
                        null,
                        new XPlayfabWrapper.CreatePlayfabOrderCallback() {
                            @Override
                            public void onSuccess(@NotNull String paystationToken, @NotNull String playfabOrderId) {
                                createOrderListener.onOrderCreated(paystationToken);
                                ViewUtils.enable(v);
                            }

                            @Override
                            public void onFailure(@NotNull String errorMessage) {
                                createOrderListener.onFailure(errorMessage);
                                ViewUtils.enable(v);
                            }
                        });
            });
        }

        private void initForVirtualCurrency(Store.VirtualCurrencyPack item) {
            Store.Price price = item.getVirtualPrices().get(0);
            buyButton.setOnClickListener(v -> {
                ViewUtils.disable(v);
                Store.buyForVirtualCurrency(
                        item.getSku(),
                        price.getCurrencyId(),
                        price.getAmount(),
                        new Store.BuyForVirtualCurrencyCallback() {
                            @Override
                            public void onSuccess() {
                                buyForVirtualCurrencyListener.onSuccess();
                                ViewUtils.enable(v);
                            }

                            @Override
                            public void onFailure(@NotNull String errorMessage) {
                                buyForVirtualCurrencyListener.onFailure(errorMessage);
                                ViewUtils.enable(v);
                            }
                        });
            });
        }

    }

}

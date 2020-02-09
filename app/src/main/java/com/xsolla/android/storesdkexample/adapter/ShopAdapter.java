package com.xsolla.android.storesdkexample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xsolla.android.sdk.XsollaSDK;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<VirtualItemsResponse.Item> items;

    public ShopAdapter(List<VirtualItemsResponse.Item> items) {
        this.items = items;
    }

    public void setItems(List<VirtualItemsResponse.Item> items) {
        this.items = items;
        notifyDataSetChanged();
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
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            buyButton = itemView.findViewById(R.id.buy_button);
        }

        private void bind(final VirtualItemsResponse.Item item) {
            Glide.with(itemView).load(item.getImageUrl()).into(itemIcon);
            itemName.setText(item.getName());

            String price = item.getPrice().getAmount();
            String currency = item.getPrice().getCurrency();
            String formattedPrice = price.substring(0, price.indexOf(".") + 3) + " " + currency;

            itemPrice.setText(formattedPrice);

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PaymentOptions paymentOptions = new PaymentOptions().create()
                            .setSandbox(true)
                            .build();


                    XStore.createOrderByItemSku(item.getSku(), paymentOptions, new XStoreCallback<CreateOrderResponse>() {
                        @Override
                        protected void onSuccess(CreateOrderResponse response) {
                            String orderId = String.valueOf(response.getOrderId());
                            String token = response.getToken();
                            Toast.makeText(itemView.getContext(), orderId, Toast.LENGTH_SHORT).show();
                            XsollaSDK.createPaymentForm(itemView.getContext(), token, true);
                        }

                        @Override
                        protected void onFailure(String errorMessage) {
                            Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

}

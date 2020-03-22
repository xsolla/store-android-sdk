package com.xsolla.android.storesdkexample.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.xsolla.android.sdk.XsollaSDK;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.common.Price;
import com.xsolla.android.store.entity.response.common.VirtualPrice;
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.List;

public class DetailFragment extends BaseFragment {

    private RadioGroup radioGroup;
    private TextView checkoutButton;

    private VirtualItemsResponse.Item item;

    private int checkedIndex = -1;

    @Override
    public int getLayout() {
        return R.layout.fragment_detail;
    }

    public static DetailFragment newInstance(VirtualItemsResponse.Item item) {

        Bundle args = new Bundle();
        args.putParcelable("item", item);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initUI() {
        ImageView itemIcon = rootView.findViewById(R.id.item_icon);
        Glide.with(getContext()).load(item.getImageUrl()).into(itemIcon);

        TextView itemName = rootView.findViewById(R.id.item_name);
        itemName.setText(item.getName());

        radioGroup = rootView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);
            checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
        });

        checkoutButton = rootView.findViewById(R.id.checkout_button);

        initToolbar();
        getBalance();
        initPaymentMethodSelector();
        initCheckoutButton();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable("item");
    }

    private void initToolbar() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(item.getName());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

    private void getBalance() {

        XStore.getVirtualBalance(new XStoreCallback<VirtualBalanceResponse>() {
            @Override
            protected void onSuccess(VirtualBalanceResponse response) {
                updateBalanceContainer(response.getItems());
            }

            @Override
            protected void onFailure(String errorMessage) {

            }
        });
    }

    private void updateBalanceContainer(List<VirtualBalanceResponse.Item> items) {
        LinearLayout balanceContainer = rootView.findViewById(R.id.balance_container);

        for (VirtualBalanceResponse.Item item : items) {
            ImageView balanceIcon = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
            layoutParams.setMargins(100, 0, 20, 0);
            balanceIcon.setLayoutParams(layoutParams);
            Glide.with(getContext()).load(item.getImageUrl()).into(balanceIcon);

            balanceContainer.addView(balanceIcon);

            TextView balanceAmount = new TextView(getContext());
            balanceAmount.setText(String.valueOf(item.getAmount()));
            balanceAmount.setTextSize(16f);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 20, 0);

            balanceContainer.addView(balanceAmount);
        }
    }

    private void initPaymentMethodSelector() {
        Typeface font = ResourcesCompat.getFont(getContext(), R.font.roboto);

        if (item.getPrice() != null) {
            RadioButton button = new RadioButton(getContext());
            button.setText(item.getPrice().getPrettyPrintAmount());
            button.setTextSize(24f);
            button.setTypeface(font);
            radioGroup.addView(button);
        }

        List<VirtualPrice> virtualPrices = item.getVirtualPrices();
        for (VirtualPrice price : virtualPrices) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(price.getPrettyPrintAmount());
            radioButton.setTextSize(24f);
            radioButton.setTypeface(font);
            radioGroup.addView(radioButton);
        }
    }

    private void initCheckoutButton() {
        checkoutButton.setOnClickListener(v -> {
            if (checkedIndex == -1) {
                showSnack("Please select payment method");
                return;
            }

            Price realPrice = item.getPrice();

            if (realPrice != null) {
                if (checkedIndex == 0) {
                    buyWithRealCurrency();
                } else {
                    buyWithVirtualCurrency(checkedIndex - 1);
                }
            } else {
                buyWithVirtualCurrency(checkedIndex);
            }
        });
    }

    private void buyWithRealCurrency() {
        PaymentOptions options = new PaymentOptions().create()
                .setSandbox(true)
                .build();

        XStore.createOrderByItemSku(item.getSku(), options, new XStoreCallback<CreateOrderResponse>() {
            @Override
            protected void onSuccess(CreateOrderResponse response) {
                String token = response.getToken();
                XsollaSDK.createPaymentForm(getContext(), token, true);
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    private void buyWithVirtualCurrency(int virtualPriceIndex) {
        VirtualPrice virtualPrice = item.getVirtualPrices().get(virtualPriceIndex);

        XStore.createOrderByVirtualCurrency(item.getSku(), virtualPrice.getSku(), new XStoreCallback<CreateOrderByVirtualCurrencyResponse>() {
            @Override
            protected void onSuccess(CreateOrderByVirtualCurrencyResponse response) {
                showSnack("Purchased by Virtual currency");
                openFragment(new MainFragment());
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

}

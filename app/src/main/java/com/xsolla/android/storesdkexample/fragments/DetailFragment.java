package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends BaseFragment {

    private List<VirtualItemsResponse.Item> currencies;
    private Spinner currencyDropdown;
    private TextView balanceLabel;

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
        currencyDropdown = rootView.findViewById(R.id.vc_dropdown);
        balanceLabel = rootView.findViewById(R.id.balance_label);
        getCurrencyList();
        initTest();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VirtualItemsResponse.Item item = getArguments().getParcelable("item");
        Log.d("Parcel", "get");
    }

    private void getCurrencyList() {
        /*XStore.getVirtualBalance(new XStoreCallback<VirtualBalanceResponse>() {
            @Override
            protected void onSuccess(VirtualBalanceResponse response) {
                currencies = response.getItems();
                initCurrencyDropdown();
            }

            @Override
            protected void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void initCurrencyDropdown() {
        /*List<String> currenciesNames = new ArrayList<>();
        for (VirtualBalanceResponse.Item item : currencies) {
            currenciesNames.add(item.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currenciesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencyDropdown.setAdapter(adapter);

        currencyDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String balance = String.valueOf(currencies.get(position).getAmount());
                balanceLabel.setText(balance);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    private void initTest() {
        String[] countries = new String[] {"Russia", "Canada", "USA", "China"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                countries
        );

        AutoCompleteTextView editTextFilledExposedDropdown = rootView.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), countries[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}

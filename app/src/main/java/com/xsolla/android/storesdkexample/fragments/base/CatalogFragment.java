package com.xsolla.android.storesdkexample.fragments.base;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.CartFragment;

public abstract class CatalogFragment extends BaseFragment {

    private Toolbar toolbar;

    protected void setupToolbar(String title) {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

    protected void setupBadge(int count) {
        MenuItem cartItem = toolbar.getMenu().findItem(R.id.action_cart);
        View actionView = cartItem.getActionView();
        TextView cartItemCount = actionView.findViewById(R.id.cart_badge);

        if (count == 0) {
            cartItemCount.setVisibility(View.GONE);
            actionView.setOnClickListener(v -> showSnack("Your cart is empty"));
        } else {
            cartItemCount.setVisibility(View.VISIBLE);
            cartItemCount.setText(String.valueOf(count));
            actionView.setOnClickListener(v -> openFragment(new CartFragment()));
        }
    }

}

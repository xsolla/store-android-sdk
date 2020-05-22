package com.xsolla.android.storesdkexample.fragments.base;

import com.xsolla.android.storesdkexample.R;

import androidx.appcompat.widget.Toolbar;

public abstract class CatalogFragment extends BaseFragment {

    private Toolbar toolbar;

    protected void setupToolbar(String title) {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

}

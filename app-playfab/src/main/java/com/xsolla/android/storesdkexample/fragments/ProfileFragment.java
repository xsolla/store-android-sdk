package com.xsolla.android.storesdkexample.fragments;

import android.view.View;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.data.auth.Auth;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

public class ProfileFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initUI() {

        rootView.findViewById(R.id.group_info).setVisibility(View.GONE);

        rootView.findViewById(R.id.logout_button).setOnClickListener(v -> {
            Auth.INSTANCE.logout();
            openRootFragment();
        });
    }

}

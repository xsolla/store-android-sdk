package com.xsolla.android.storesdkexample.fragments;

import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

public class ProfileFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initUI() {
        TextView username = rootView.findViewById(R.id.username);
        TextView email = rootView.findViewById(R.id.email);
        TextView tokenType = rootView.findViewById(R.id.token_type);
        TextView publisherId = rootView.findViewById(R.id.publisher_id);
        TextView issuedAt = rootView.findViewById(R.id.issued_at);
        TextView expiresAt = rootView.findViewById(R.id.expires_at);
        TextView issuer = rootView.findViewById(R.id.issuer);

        if (!BuildConfig.USE_OAUTH) {

            JWT jwt = XLogin.getJwt();

            username.setText(jwt.getClaim("username").asString());
            email.setText(jwt.getClaim("email").asString());
            tokenType.setText(jwt.getClaim("type").asString());
            publisherId.setText(jwt.getClaim("publisher_id").asString());
            issuedAt.setText(jwt.getIssuedAt().toString());
            expiresAt.setText(jwt.getExpiresAt().toString());
            issuer.setText(jwt.getIssuer());

        }

        rootView.findViewById(R.id.logout_button).setOnClickListener(v -> {
            XLogin.logout();
            openRootFragment();
        });
    }

}

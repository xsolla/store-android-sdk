package com.xsolla.android.xsolla_login_sdk.entity.request;

public class LoginUser {
    private String username;
    private String password;
    private boolean remember_me;

    public LoginUser(String username, String password, boolean remember_me) {
        this.username = username;
        this.password = password;
        this.remember_me = remember_me;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember_me() {
        return remember_me;
    }

    public void setRemember_me(boolean remember_me) {
        this.remember_me = remember_me;
    }
}

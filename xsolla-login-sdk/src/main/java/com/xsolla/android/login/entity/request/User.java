package com.xsolla.android.login.entity.request;

public class User {
    private String username;
    private String password;
    private boolean remember_me;

    public User(String username, String password) {
        this(username, password, false);
    }

    public User(String username, String password, boolean remember_me) {
        this.username = username;
        this.password = password;
        this.remember_me = remember_me;
    }

}

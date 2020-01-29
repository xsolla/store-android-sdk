package com.xsolla.android.login.entity.request;

public class NewUser {

    private String username;
    private String email;
    private String password;

    public NewUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}

package com.xsolla.android.login.entity.request;

public class RegisterUserBody {

    private String username;
    private String email;
    private String password;

    public RegisterUserBody(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}

# Xsolla Login Android SDK

**Xsolla Login Android SDK** is used to integrate Xsolla Login, a single sign-on tool that uses API methods to authenticate and secure user passwords. This creates a seamless one-click registration experience players can use for fast and safe transactions across all of your games. Create a  [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) with Xsolla to get started.

## Install
The library is available in JCenter. To start using it, add the following line to the dependencies section of your `build.gradle` file:

```groovy
implementation 'com.xsolla.android:login:0.9.0'
```

# Usage

## Initialize SDK
Initialize Xsolla Login SDK

```java
XLogin.init("login-project-id", context);
```
Or if you changed `Callback URL` in your Publisher Account
```java
XLogin.init("login-project-id", "your-callback-url", context);
```

## Register User

```java
XLogin.register("username", "email", "password", new XStoreCallback<Void>() {
        @Override
        protected void onSuccess(Void response) {
            // Registration success. Show the user an explanation to check email and confirm account.
        }

        @Override
        protected void onFailure(String errorMessage) {
            // Something went wrong. Reason is passed in errorMessage
        }
});
```
See example in [RegisterFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/RegisterFragment.java) from Sample App.

## Auth by Username and Password

```java
XLogin.login(username, password, new XStoreCallback<AuthResponse>() {
        @Override
        protected void onSuccess(AuthResponse response) {
            // User is authenticated. Token retrieved, saved in SDK, and passed here.
            String token = response.getToken();
        }

        @Override
        protected void onFailure(String errorMessage) {
            // Something went wrong. Reason is passed in errorMessage
        }
});
```
See example in [AuthFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/AuthFragment.java) from Sample App.

## Auth via Social Network
**NOTE** Default callback URL is `https://login.xsolla.com/api/blank`

If you changed `Callback Url` in your Publisher account, then you should init SDK like this:
```java
XLogin.getInstance().init("login-project-id", "your-callback-url", this);
```
Available Social Networks:
* Google
* Facebook
* Twitter
* Linkedin
* Naver
* Baidu

```java
XLogin.loginSocial(SocialNetwork.GOOGLE, new XLoginSocialCallback<SocialAuthResponse>() {
    @Override
    protected void onSuccess(SocialAuthResponse response) {
        // User is authenticated. Token retrieved, saved in SDK, and passed here.
        String token = response.getToken();
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Something went wrong. Reason is passed in errorMessage
    }

    @Override
    protected Activity getActivityForSocialAuth() {
        // Provide an activity to show authentication web page within
        return getActivity();
    }
});
```
See example in [AuthFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/AuthFragment.java) from Sample App.

## Reset Password

```java
XLogin.resetPassword("username", new XStoreCallback<Void>() {
    @Override
    protected void onSuccess(Void response) {
        // Password reset success. Show the user an explanation to check email and set new password.
        // **NOTE** You will receive success callback even when specific user doesn't exist!
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Something went wrong. Reason is passed in errorMessage
    }
});
```
See example in [ResetPasswordFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/ResetPasswordFragment.java) from Sample App.

## Logout
```java
XLogin.logout(); // Token is cleared
```

# Token and User info
At any time you can get a token from SDK
```java
String token = XLogin.getToken(); // Token or `null` if token isn't saved
```

Check if either token valid or not
```java
XLogin xLogin = XLogin.getInstance();
boolean isTokenValid = xLogin.isTokenValid();
```

## User info
You can get all available information from the token according to JWT Specification. For more information see [https://jwt.io/]

See example in [RegisterFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/RegisterFragment.java) from Sample App.

Get JWT
```java
JWT jwt = XLogin.getJWT();
```
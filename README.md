# Xsolla Login Android Asset

**Xsolla Login Android Asset** is used to integrate Xsolla Login, a single sign-on tool that uses API methods to authenticate and secure user passwords. This creates a seamless one-click registration experience players can use for fast and safe transactions across all of your games. Create a  [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) with Xsolla to get started. The asset includes the following SDKs:

* Xsolla Login Android SDK

## Install
The library is available both in Maven Central and JCenter. To start using it, add this line to your `build.gradle` dependencies file:

```groovy
// TODO Publish to Maven Central and JCenter
```

# Usage

## Initialize SDK
Initialize Xsolla Login SDK in MainActivity

```java
XLogin.init("login-project-id", this);
```
Or if you changed `Callback Url` in your Publisher Account
```java
XLogin.init("login-project-id", "your-callback-url", this);
```

## Register User

```java
XLogin.registerUser("username", "email", "password", new XStoreCallback<Void>() {
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
XLogin.loginSocial(SocialNetwork.GOOGLE, new XStoreCallback<SocialAuthResponse>() {
    @Override
    protected void onSuccess(SocialAuthResponse response) {
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
XLogin xLogin = XLogin.getInstance();
String token = xLogin.getToken(); // Token or `null` if token isn't saved
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
XLogin xLogin = XLogin.getInstance();
JWT jwt = xLogin.getJwt();
```

### Registered Claims

#### Issuer ("iss")

Returns the Issuer value or null if it's not defined.

```java
String issuer = jwt.getIssuer();
```

#### Subject ("sub")

Returns the Subject value or null if it's not defined.

```java
String subject = jwt.getSubject();
```

#### Audience ("aud")

Returns the Audience value or an empty list if it's not defined.

```java
List<String> audience = jwt.getAudience();
```

#### Expiration Time ("exp")

Returns the Expiration Time value or null if it's not defined.

```java
Date expiresAt = jwt.getExpiresAt();
```

#### Not Before ("nbf")

Returns the Not Before value or null if it's not defined.

```java
Date notBefore = jwt.getNotBefore();
```

#### Issued At ("iat")

Returns the Issued At value or null if it's not defined.

```java
Date issuedAt = jwt.getIssuedAt();
```

### Private Claims

Additional Claims defined in the token can be obtained by calling `getClaim` and passing the Claim name. If the claim can't be found, a BaseClaim will be returned. BaseClaim will return null on every method call except for the `asList` and `asArray`.

```java
Claim claim = jwt.getClaim("isAdmin");
```

You can also obtain all the claims at once by calling `getClaims`.

```java
Map<String, Claim> allClaims = jwt.getClaims();
```

### Claim Class
The Claim class is a wrapper for the Claim values. It allows you to get the Claim as different class types. The available helpers are:

#### Primitives
* **asBoolean()**: Returns the Boolean value or null if it can't be converted.
* **asInt()**: Returns the Integer value or null if it can't be converted.
* **asLong()**: Returns the Long value or null if it can't be converted.
* **asDouble()**: Returns the Double value or null if it can't be converted.
* **asString()**: Returns the String value or null if it can't be converted.
* **asDate()**: Returns the Date value or null if it can't be converted. Note that the [JWT Standard](https://tools.ietf.org/html/rfc7519#section-2) specified that all the *NumericDate* values must be in seconds. (Epoch / Unix time)

#### Collections
To obtain a Claim as a Collection, you'll need to provide the **Class Type** of the contents to convert from.

* **asArray(class)**: Returns the value parsed as an Array of elements of type **Class Type**, or an empty Array if the value isn't an JSON Array.
* **asList(class)**: Returns the value parsed as a List of elements of type **Class Type**, or an empty List if the value isn't an JSON Array.

# Xsolla Paystation Android SDK

**Xsolla Paystation Android SDK** allows partners to monetize their product by providing users with a convenient UI to pay for in-game purchases in the game store Create a  [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) with Xsolla to get started.

## Install
The library is available in JCenter. To start using it, add the following line to the dependencies section of your `build.gradle` file:

```groovy
implementation 'com.xsolla.android:paystation:0.9.0'
```

# Usage

## Create Paystation intent

```java
Intent intent = XPaystation.createIntentBuilder(getContext())
            .accessToken(new AccessToken(token))
            .isSandbox(BuildConfig.IS_SANDBOX)
            .build();
```

## Start activity using created intent

```java
startActivityForResult(intent, RC_PAYSTATION);
```
## Parse activity result

```java
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_PAYSTATION) {
        XPaystation.Result result = XPaystation.Result.fromResultIntent(data);
    }
}
```

See example in [DetailFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/DetailFragment.java) from Sample App.

> [!TIP]
> Integrate [Xsolla Mobile SDK](https://developers.xsolla.com/sdk/mobile/) to enable In-App Payments across mobile platforms within and outside of stores in a platform-compliant way, powered by Pay Station with over 700 payment methods.

# Enterprise-level Xsolla SDK for Android

Xsolla SDK for Android is a ready-to-use library for applications and games that allows you to easily embed [Xsolla services](https://developers.xsolla.com/#get_started).

![android sdk image](https://i.imgur.com/NshUN8S.png "android sdk image")


The SDK includes the following libraries:

* [Login](https://github.com/xsolla/store-android-sdk/tree/master/xsolla-login-sdk) —  allows authenticating users and manage user attributes
* [Store](https://github.com/xsolla/store-android-sdk/tree/master/xsolla-store-sdk) — allows managing your in-game store
* [Inventory](https://github.com/xsolla/store-android-sdk/tree/master/xsolla-inventory-sdk) — allows managing player inventory
* [Payments](https://github.com/xsolla/store-android-sdk/tree/master/xsolla-payments-sdk) — allows using payment UI

You can integrate all of these libraries or specific libraries that meet your needs into your project.

[Try our Sample App to learn more](https://github.com/xsolla/store-android-sdk/tree/master/app).


## System requirements

* Android OS 5.0 or higher
* Internet connection

## Install library

The library are available in Maven Central. 
* [Login library](https://search.maven.org/artifact/com.xsolla.android/login)
* [Store library](https://search.maven.org/artifact/com.xsolla.android/store)
* [Inventory library](https://search.maven.org/artifact/com.xsolla.android/inventory)
* [Payments library](https://search.maven.org/artifact/com.xsolla.android/payments)

To install the necessary library:

1. Start Android Studio.
2. Open `build.gradle` file of your application.
3. Add the following line to the dependencies section, where `<version_number>` is the required version of the library. Example for Store library:

```
implementation 'com.xsolla.android:store:<version_number>'
```

Follow the [documentation](https://developers.xsolla.com/sdk/android/) to initialize library and configure project on Xsolla side.

## Legal info

Explore [pricing information](https://xsolla.com/pricing) and [Privacy Policy](https://xsolla.com/privacypolicy) that helps you work with Xsolla.

Xsolla offers the necessary tools to help you build and grow your gaming business, including personalized support at every stage. The terms of payment are determined by the contract that can be signed via Publisher Account.

**The cost of using all Xsolla products is 5% of the amount you receive for the sale of the game and in-game goods via Xsolla Pay Station**. If you do not use Xsolla Pay Station in your application, but use other products, contact your Account Manager to clarify the terms and conditions.

--

## License

[See the LICENSE file](https://github.com/xsolla/store-android-sdk/blob/master/LICENSE-2.0.txt).


## Additional resources

* [Xsolla official website](https://xsolla.com/)
* [Developers documentation](https://developers.xsolla.com/sdk/android/)
* [Code reference](https://developers.xsolla.com/sdk-code-references/android-store/)
* API reference:
    * [Pay Station API](https://developers.xsolla.com/pay-station-api/)
    * [Login API](https://developers.xsolla.com/login-api/)
    * [In-Game Store & Buy Button API](https://developers.xsolla.com/commerce-api/)

> [!TIP]
> Integrate [Xsolla Mobile SDK](https://developers.xsolla.com/sdk/mobile/) to enable In-App Payments across mobile platforms within and outside of stores in a platform-compliant way, powered by Pay Station with over 700 payment methods.

The Inventory library is used to integrate applications based on Android with [In-Game Store](https://developers.xsolla.com/doc/in-game-store/features/player-inventory/) for managing:

* user inventory
* virtual currency balance
* cross-platform inventory


## Integration

Before you integrate the library, you need to sign up to [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) and set up a new project.

The library is available in [Maven Central](https://search.maven.org/artifact/com.xsolla.android/inventory). To install it, add the following line to the dependencies section, where `<version_number>` is the required version of the Inventory library:

```
implementation 'com.xsolla.android:inventory:<version_number>'
```

More instructions are on the [Xsolla Developers portal](https://developers.xsolla.com/sdk/android/inventory/).


## Usage

To manage the features of Xsolla products, the library contains a set of classes that let you make requests to the [In-Game Store & Buy Button API](https://developers.xsolla.com/commerce-api/player-inventory).

After installing and initializing the Store library, implement user authorization logic in your application using [SDK methods](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Inventory+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0).


## Additional resources



* [Xsolla official website](https://xsolla.com/)
* [Developers documentation](https://developers.xsolla.com/sdk/android/login/)
* [Code reference](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Inventory+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0)
* [In-Game Store & Buy Button API](https://developers.xsolla.com/commerce-api/player-inventory)
> [!TIP]
> Integrate [Xsolla Mobile SDK](https://developers.xsolla.com/sdk/mobile/) to enable In-App Payments across mobile platforms within and outside of stores in a platform-compliant way, powered by Pay Station with over 700 payment methods.

The Payments library is used to integrate applications based on Android with [Xsolla Pay Station](https://developers.xsolla.com/doc/pay-station/).

Main features:

* purchase for 130+ currencies
* purchase via 700+ payment methods
* built-in anti-fraud
* payment UI localized in 20 languages
* purchase refund

## Integration

Before you integrate the library, sign up to [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) and set up a new project.

The library is available in [Maven Central](https://search.maven.org/artifact/com.xsolla.android/payments). To install it, add the following line to the dependencies section where `<version_number>` is the required version of the Payments library:

```
implementation 'com.xsolla.android:payments:<version_number>'
```


More instructions are on the [Xsolla Developers portal](https://developers.xsolla.com/sdk/android/payments/).



## Usage

To manage the features of Xsolla products, the library contains a set of classes that let you make requests to the [Pay Station API](https://developers.xsolla.com/pay-station-api/).

After installing and initializing the Store library, implement user authorization logic in your application using [SDK methods](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Payments+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0).


## Additional resources

* [Xsolla official website](https://xsolla.com/)
* [Developers documentation](https://developers.xsolla.com/sdk/android/payments/)
* [Code reference](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Payments+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0)
* [Pay Station API](https://developers.xsolla.com/pay-station-api/)
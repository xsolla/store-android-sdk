> [!TIP]
> Integrate [Xsolla Mobile SDK](https://developers.xsolla.com/sdk/mobile/) to enable In-App Payments across mobile platforms within and outside of stores in a platform-compliant way, powered by Pay Station with over 700 payment methods.

To get started with the basic SDK features, try sample scripts. Samples cover the following user scenarios:
* Login via username/email and password
* Social login
* Display of item catalog
* Selling virtual items for real currency
* Selling virtual items for virtual currency
* Display of virtual currency balance
* Display of items in inventory

To run and test samples, you must change `AndroidManifest.xml` of the [Sample App](https://github.com/xsolla/store-android-sdk/tree/master/app):

1. Open the `<ProjectPath>\app\src\main\AndroidManifest.xml` file with any text editor.
2. Find the `activity` tag that starts when you run the application (it contains `intent-filter`).
3. In the `android:name` attribute, specify `activity` of the sample script. Provide the full path along with the package, for example: `com.xsolla.android.samples.display.InventoryItemsActivity` or `com.xsolla.android.samples.display.LoginActivity`.

## Contacts

* [Support team and feedback](https://xsolla.com/partner-support)
* [Integration team](mailto:integration@xsolla.com)


## Additional resources

* [Xsolla official website](https://xsolla.com/)
* [Developer documentation](https://developers.xsolla.com/sdk/android/)
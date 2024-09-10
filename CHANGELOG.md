# Changelog
## [6.0.10] - Login SDK - 2024-09-11 
### Changed
- `authenticateViaDeviceId` SDK method. Added `deviceId` optional parameter.

### Fixed
- Fixed an issue where the login via `startSocialAuth` or `startAuthWithXsollaWidget` methods displays an error in webView.

## [2.2.14] - Demo Apps - 2024-09-11  
### Fixed  
- Fixed inappropriate error message after registration.

## [2.5.10] - Store SDK - 2024-08-21  
### Changed  
- `createOrderFromCartById`, `createOrderFromCurrentCart`, and `createOrderByItemSku` SDK methods.  Added an optional parameter `externalTransactionToken`.  
  
## [1.4.1] - Payments SDK - 2024-08-21 
### Changed  
- `XPayments.IntentBuilder` class. Added new callbacks:  
    - `payStationClosedCallback` —  handles the closure of the payment UI. It includes the `isManually` parameter, which specifies whether the UI was closed manually or automatically.  
    - `statusReceivedCallback` — handles changes in payment status. It receives the `InvoicesDataResponse` object, which contains an array of invoices. If any invoice has a status of `DONE`, it implies a successful purchase.  

### Added  
- `getStatus` SDK method. Returns invoice data for a specified order.  
  
### Fixed  
- Fixed a crash that occurs when `activityType` is set to Custom Tabs, but they are not supported by the user’s device.  
- Redirects for Web Views are now case-insensitive.  
  
## [2.2.13] - Demo Apps - 2024-08-21  
### Fixed  
- Fixed an issue where the catalog was empty after closing the bundle preview.

## [2.5.9] - Store SDK - 2024-07-25
### Changed
- `PaymentOptions` data. 
    - Added `country` parameter, that defines the Pay Station language and list of payments methods

## [6.0.9] - Login SDK - 2024-07-25 
### Changed
- Authentication with Xsolla widget now supports OAuth2 and access token refresh logic.

## [2.5.7] - Store SDK - 2024-07-11
### Changed
- Payments requests (`UiProjectSetting` class). Added following parameters:
    - `gpQuickPaymentButton` — defines the way the Google Pay payment method is displayed. If true, the button for quick payment via Google Pay is displayed at the top of the payment UI. If `true`, Google Pay is displayed in the list of payment methods according to the PayRank algorithm. `true` by default.
    - `closeButtonIcon` — defines the icon of the **Close** button in the payment UI. Can be `arrow` or `cross`. `cross` by default. 
  
## [1.4.0] - Payments SDK - 2024-07-11  
### Added  
- Support of Trusted Web activity as the way to display Pay Station. Learn more: https://developer.chrome.com/docs/android/trusted-web-activity/ 

### Fixed  
- Null pointer exception in `onCustomTabsServiceConnected` method of `CustomTabsHelper` class  
  
## [6.0.8] - Login SDK - 2024-07-11  
### Fixed  
- Crash while authorizing via Xsolla Widget in WebView

## [2.2.12] - Demo Apps - 2024-07-11
### Added
- Trusted Web activity as the default way to display Pay Station

## [2.5.6] - Store SDK - 2024-05-06
### Changed
- `getVirtualItems` SDK method. Added the `requestGeoLocale` parameter. If `true`, the response returns the locale in the `geoLocale` parameter.

## [1.3.4] - Payments SDK - 2024-05-06
### Added
- Pay Station preloader. Allows faster content display in WebView and Custom Tabs.

## [2.2.11] - Demo Apps - 2024-04-11
### Fixed
- Token refresh on application startup

## [2.5.5] - Store SDK - 2024-04-11
### Changed
- Store library methods. The ‘has_more‘ parameter added in response

## [1.3.3] - Payments SDK - 2024-04-11
### Added
- ‘payStationVersion‘ parameter in XPayments.IntentBuilder
- Support of payments via Google Pay in WebView

## [2.2.10] - Demo Apps - 2024-03-06
### Added
- closeButton parameter for payment token request. This parameter depends on whether the default browser supports Custom Tabs or not

## [2.5.4] - Store SDK - 2024-03-06
### Changed
- Updated the parameters of all queries for analytics on the Xsolla side
- By default, the locale is determined by the user's IP

## [2.0.4] - Inventory SDK - 2024-03-06
### Changed
- Updated the parameters of all queries for analytics on the Xsolla side

## [6.0.7] - Login SDK - 2024-03-06
### Added
- Custom Tabs support for the Huawei browser and Samsung Internet browser

### Changed
- Updated the parameters of all queries for analytics on the Xsolla side

## [1.3.2] - Payments SDK - 2024-03-06
### Added
- Custom Tabs support for the Samsung Internet browser

### Changed
- Updated the parameters of all queries for analytics on the Xsolla side

## [1.3.1] - Payments SDK - 2024-02-09
### Fixed
- Redirection to the application after payment using some payment methods

## [6.0.6] - Login SDK - 2024-01-30
### Changed
- ‘startAuthWithXsollaWidget’ SDK method. Added the ‘locale’ parameter

## [6.0.5] - Login SDK - 2024-01-19
### Added
- ApiHost parameter for LoginConfig

## [2.2.9] - Demo Apps - 2024-01-19
### Added
- LOGIN_API_HOST config parameter in demo

## [1.3.0] - Payments SDK - 2024-01-16
### Added
- Support of all redirections in WebView
### Changed
- Fixed bug with storage permissions for Android 13 (API level 33)

## [2.2.7] - Demo Apps - 2023-12-27
### Added
- Support of Alipay redirections and APK download in demo

## [1.2.0] - Payments SDK - 2023-12-27
### Added
- Support of Alipay redirections and APK download in WebView

## [1.1.0] - Payments SDK - 2023-11-17
### Added
- Custom Tabs support for the Huawei browser

## [2.2.6] - Demo Apps - 2023-11-01
### Added
- Samples

## [2.5.3] - Store SDK - 2023-11-01
### Changed
- Names of default Pay Station themes are replaced by the theme ID

## [6.0.4] - Login SDK - 2023-08-22
### Changed
-  Fixed `setTokenData` parameters

## [1.0.6] - Payments SDK - 2023-08-07
### Changed
- Fixed false positive payment result detection when payment was declined from merchant side

## [6.0.3] - Login SDK - 2023-07-28
### Added
- `refreshToken`, `tokenExpireTime` and `setTokenData` SDK methods.

## [2.5.0] - Store SDK - 2023-07-24
### Added
- `startAuthWithXsollaWidget` and `finishAuthWithXsollaWidget` SDK methods. They allow to open the Login widget in the internal browser

## [2.4.0] - Store SDK - 2023-06-19
### Changed
- Added `ItemLimits` data to store items responses
- Added ability to pass `externalId` data to purchase order requests
- Added methods to purchase free items

## [1.0.5] - Payments SDK - 2023-06-19
### Changed
- Fixed false positive payment result detection when payment was declined from merchant side

## [2.2.3] - Demo Apps - 2023-06-19
### Changed
- Added free items support in demo

## [6.0.1] - Login SDK - 2023-06-19
- Improved and actualized method annotations

## [2.0.3] - Inventory SDK - 2023-06-19
- Improved and actualized method annotations

## [6.0.0] - Login SDK - 2023-02-23
### Changed
- Updated `updateCurrentUserDetails` method. Removed the `birthday` parameter.

### Fixed
- Facebook native authentication
- Updating user details in the Sample App

## [2.3.0] - Store SDK - 2023-02-23
### Changed
- Updated order status tracking mechanism
- Integrated Centrifuge SDK

## [2.2.2] - Demo Apps - 2023-02-23
### Changed
- Removed Birthday field from Account screen

## [5.1.0] - Login SDK - 2023-01-06
### Changed
- Updated WeChat SDK
- Fixed WeChat native authentication


## [5.0.1] - Login SDK - 2022-11-03
### Changed
- Migrated network layer from Retrofit to Ktor Client
- Updated Target and Compile SDK to 33 (Android 13)
- Updated WeChat SDK and removed Jcenter repository requirement

## [2.2.0] - Store SDK - 2022-11-03
### Changed
- Updated Target and Compile SDK to 33 (Android 13)
### Added
- Promotions related fields for catalog responses

## [2.0.2] - Inventory SDK - 2022-11-03
### Changed
- Updated Target and Compile SDK to 33 (Android 13)

## [1.0.4] - Payments SDK - 2022-11-03
### Changed
- Updated Target and Compile SDK to 33 (Android 13)

## [2.2.0] - Demo Apps - 2022-11-03
### Added
- Device ID login
### Changed
- Updated Target and Compile SDK to 33 (Android 13)


## [5.0.0] - Login SDK - 2022-09-06
### Added
- Consumer ProGuard rules
- Locale argument for methods triggering email sending
### Changed
- Social authentication error parsing fixed
### Removed
- JWT authentication

## [2.1.0] - Store SDK - 2022-09-06
### Added
- Consumer ProGuard rules
- Unauthenticated mode

## [2.0.1] - Inventory SDK - 2022-09-06
### Added
- Consumer ProGuard rules

## [1.0.3] - Payments SDK - 2022-09-06
### Added
- Consumer ProGuard rules

## [2.1.1] - Demo Apps - 2022-09-06
### Added
- Demo user generation
### Changed
- Minor UI bugs fixed
- Obfuscation and shrinking enabled


## [4.0.0] - Login SDK - 2022-07-04
### Changed
- List of social networks available for linking is equal to the list of them for authentication

## [2.1.0] - Demo Apps - 2022-07-04
### Removed
- Character and Level Up functionality


## [1.0.2] - Payments SDK - 2022-06-22
### Changed
- Updated dependencies

## [2.0.0] - Store SDK - 2022-06-22
### Added
- Improved test coverage
### Changed
- Subscriptions renamed to time limited items
- Updated dependencies
- ps4-default-dark set as default theme for order creation

## [2.0.0] - Inventory SDK - 2022-06-22
### Added
- Improved test coverage
### Changed
- Subscriptions renamed to time limited items
- Updated dependencies

## [3.1.1] - Login SDK - 2022-06-22
### Added
- Improved test coverage
### Changed
- Updated dependencies

## [2.0.0] - Demo Apps - 2022-06-22
### Added
- More social authentication providers
### Changed
- Updated dependencies
### Removed
- Inventory and Custom Auth Demo


## [1.0.1] - Payments SDK - 2022-02-21
- Updated target sdk version to 31
- Fixed a bug with status after payment completion

## [1.1.1] - Store SDK - 2022-02-21
- Updated target sdk version to 31

## [1.0.4] - Inventory SDK - 2022-02-21
- Updated target sdk version to 31

## [3.1.0] - Login SDK - 2022-02-21
- Updated target sdk version to 31
- Added new methods for social account linking
- Fixed a bug with status after sign in completion


## [1.0.0] - Payments SDK - 2022-02-04
- Updated deep links configuration mechanism
- Updated browser selection algorithm

## [3.0.0] - Login SDK - 2022-02-04
- Updated deep links configuration mechanism
- Updated browser selection algorithm


## [1.9.0] - Demo Apps - 2021-11-30
- Added 'Web Shop' button in demo
- Removed cart from demo
- Added refresh inventory button in demo
- Updated dependencies

## [2.1.0] - Login SDK - 2021-11-30
- Added 'oauthLogout' method
- Updated Facebook SDK version
- Updated Google Play Services Auth SDK version
- Updated dependencies

## [1.0.3] - Inventory SDK - 2022-11-30
- Fixed item consume method
- Updated dependencies

## [1.1.0] - Store SDK - 2021-11-30
- Added new methods (getVirtualItemsShort, removePromocode)
- Added checking order status using WebSocket
- Updated 'createOrderByItemSku' method (added 'quantity' parameter)
- Updated dependencies

## [0.17.1] - Payments SDK - 2022-11-30
- Updated dependencies


## [2.0.0] - Login SDK - 2021-09-03

### Added

- Custom Tabs usage for social authorizations
- Passwordless authorization schemes (by phone number and email)
- Facebook SDK updated

## [1.0.0] - Store SDK - 2021-09-03

### Added

- Game Keys and Entitlement methods

## [0.17.0] - Payments SDK - 2021-09-03

### Removed

- PlayFab and Serverless integrations


## [1.2.1] - Login SDK - 2021-08-18

### Fixed

- Login API arguments


## [0.16.0] - Payments SDK - 2021-06-30

### Added

- Custom Tabs support

## [0.19.0] - Store SDK - 2021-06-30

### Added

- New Commerce API methods and arguments

## [1.2.0] - Login SDK - 2021-06-30

### Added

- Authentication via device id
- Authentication via phone number

## [0.15.3] - Payments SDK - 2021-04-13

### Fixed

- Redirect while WebView payment flow

### Added

- Library is available in Maven Central

## [0.18.1] - Store SDK - 2021-04-13

### Added

- Library is available in Maven Central

## [1.0.2] - Inventory SDK - 2021-04-13

### Added

- Library is available in Maven Central

## [1.1.5] - Login SDK - 2021-04-13

### Added

- QQ native authentication
- Facebook SDK updated
- Library is available in Maven Central


## [1.1.4] - Login SDK - 2021-02-25

### Fixed

- QQ authentication


## [1.5.2] - Demo Apps - 2021-02-24

### Added

- Renew subscription button

### Fixed

- Kotlin synthetics replaced by view bindings


## [1.1.3] - Login SDK - 2021-02-17

### Added

- WeChat native authentication


## [1.1.2] - Login SDK - 2021-01-22

### Fixed

- Unity compatibility


## [1.5.0] - Demo Apps - 2021-01-12

### Added

- Access token authentication demo app
- Web Store link in Inventory demo


## [1.1.1] - Login SDK - 2020-12-22

### Fixed

- Unreal Engine compatibility


## [1.1.0] - Login SDK - 2020-12-18

### Added

- New social networks support
- New API methods support

### Fixed

- Native first time auth with Google
- Java support

## [1.0.1] - Inventory SDK - 2020-12-18

### Fixed

- Java support

## [0.18.0] - Store SDK - 2020-12-18

### Added

- New API methods support
- Promocodes functionality

## [0.15.2] - Payments SDK - 2020-12-18

### Fixed

- Fix serverless integration naming


## [1.0.0] - Inventory SDK - 2020-12-04

### Added

- Inventory SDK release

## [0.17.0] - Store SDK - 2020-11-24

### Added

- Bundles functionality.


## [0.15.1] - Payments SDK - 2020-11-24

### Fixed

- Back to game redirects.


## [0.16.0] - Store SDK - 2020-11-16

### Added

- Coupons functionality.


## [0.15.0] - Payments SDK - 2020-11-16

### Fixed

- Payment systems redirects.


## [1.0.0] - Login SDK - 2020-11-03

### Added

- User account functionality.
- Friends system functionality.


## [0.15.0] - Store SDK - 2020-11-03

### Added

- Custom parameters.
- The ability to consume virtual currency when purchasing a virtual item or according to in-game logic.

### Fixed

- Minor bugs.


## [0.14.0] - Store SDK - 2020-08-26

### Changed

- Sample app UI.

### Added

- The support for OAuth 2.0 authorization.
- The ability to invalidate JWT.


## [0.13.1] - Store SDK - 2020-08-25

### Fixed

- Fieds visibility in API response classes.


## [0.13.0] - Store SDK - 2020-07-23

### Added

- The ability to integrate Xsolla Pay Station for processing purchases with apps that have no server part and game logic is implemented on the client side.


## [0.12.0] - Store SDK - 2020-07-10

### Added

- The module for Playfab scripts.


## [0.11.0] - Store SDK - 2020-07-08

### Added

- The ability of native authentication via social networks.


## [0.10.0] - Store SDK - 2020-06-05

### Added

- The ability to sell non-recurring subscriptions.

### Fixed

- Minor bugs.


## [0.9.0] - Store SDK - 2020-04-17

### Added

- Login demo.
- Store demo.
- Payments demo.

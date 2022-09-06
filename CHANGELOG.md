# Changelog

## [5.0.0] - Login SDK - 2022-09-06
### Added
- Consumer ProGuard rules
- locale argument for methods triggering email sending
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

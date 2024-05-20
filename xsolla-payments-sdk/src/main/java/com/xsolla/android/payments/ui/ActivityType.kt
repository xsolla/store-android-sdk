package com.xsolla.android.payments.ui

/**
 * Various activity types used for displaying `PayStation` content.
 */
enum class ActivityType {
    /**
     * A web-view based solution will be used for displaying the content.
     *
     * PROS:
     *  - seamless UX experience (no additional activity is opened over the app)
     *  - allows for screen orientation locking (n/a right now)
     *  - doesn't have a standard toolbar at the top (e.g. `CustomTabs` variant)
     *
     * CONS:
     *  - might not support natively security oriented features like Google Wallet, etc.
     */
    WEB_VIEW,

    /**
     * A Chrome Custom Tabs based solution will be used for displaying the content.
     *
     * PROS:
     *  - integrates natively with Google Wallet and other on-device payment services (auto-fill, etc)
     *
     * CONS:
     *  - opens in an overlay activity over the app
     *  - relies on Chrome and its versions when it comes to extra features support
     *  - doesn't allow for screen orientation locking
     */
    CUSTOM_TABS,

    /**
     * A Trusted Web Activity based solution is used for displaying the content.
     *
     * PROS:
     *  - integrates natively with Google Wallet and other on-device payment services (auto-fill, etc)
     *  - doesn't hava a standard toolbar at the top (e.g. `CustomTabs` variant), when run in `trusted` mode
     *  - allows for screen orientation locking, when run in `trusted` mode
     *  - supports for a smooth transition into the content (uses a customized
     *    background and a fade-out effect for this purpose)
     *
     * CONS:
     *  - some features require the `trusted` mode to be active for them to work
     *    ([see here](https://developer.chrome.com/docs/android/trusted-web-activity/integration-guide#remove-url-bar))
     */
    TRUSTED_WEB_ACTIVITY
}

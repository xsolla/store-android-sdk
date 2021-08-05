The Login library is used to integrate applications based on Android with [Xsolla Login](https://developers.xsolla.com/doc/login/).

Main features:


* authentication via email or username and password
* authentication via the following social networks:
    * Google
    * Facebook
    * Twitter
    * LinkedIn
    * Naver
    * Baidu
    * WeChat
    * QQ
* sign-up
* email confirmation
* password reset
* user attributes management
* friend system
* user account


## Integration

Before you integrate the library, sign up to [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) and set up a new project.

The library is available in [Maven Central](https://search.maven.org/artifact/com.xsolla.android/login). To install it, add the following line to the dependencies section where `<version_number>` is the required version of the Login library:

```
implementation 'com.xsolla.android:login:<version_number>'
```


More instructions are on the [Xsolla Developers portal](https://developers.xsolla.com/sdk/android/login/).


## Usage

To manage the features of Xsolla products, the library contains a set of classes that let you make requests to the [Login API](https://developers.xsolla.com/login-api/).

After installing and initializing the Login library, implement user authorization logic in your application using [SDK methods](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Login+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0).


## Additional resources



* [Xsolla official website](https://xsolla.com/)
* [Developers documentation](https://developers.xsolla.com/sdk/android/login/)
* [Code reference](https://developers.xsolla.com/sdk-code-references/android-store/#%5B.ext%2FXsolla+Login+SDK+for+Android%2F%2F%2FPointingToDeclaration%2F%5D%2FMain%2F0)
* [Login API](https://developers.xsolla.com/login-api/)
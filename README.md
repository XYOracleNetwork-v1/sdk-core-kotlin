# sdk-core-kotlin
A library to preform all basic XYO Network functions.
This includes creating an origin chain, maintaining an origin chain, negotiations for talking to other nodes, and other basic functionality.
The library has heavily abstracted modules so that all operations will work with any crypto, storage, networking, ect.

## Prerequisites
* JDK 1.8
* Kotlin
* Kotlin Coroutines

## Installing
You can add sdk-ble-android to your existing app by cloning the project and manually adding it to your build.gradle:

```
git clone git@github.com:XYOracleNetwork/sdk-core-kotlin.git
```

```gradle
dependencies {
    implementation 'com.github.XYOracleNetwork:sdk-core-kotlin:v0.1.0-beta'
}
```

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
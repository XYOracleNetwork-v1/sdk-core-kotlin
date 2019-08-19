# Sample Kotlin Project - Android Example

If you are just getting started with Kotlin and Android development

## App Structure with Kotlin
[Click here for an introduction from Google](<https://codelabs.developers.google.com/codelabs/build-your-first-android-app-kotlin/index.html?index=..%2F..index#0>)

Versions used in this sample

- Android Studio `3.4.2`
- Java `12.0.1`

## Using Android Studio

If we are using Android Studio to create this simple app, then make sure it is updated to the latest versions. 

### Virtual Device Emulator 

When creating the configuration for the emulator, we recommend using a mid-range device definition for a wider range of coverage for android devices. (Such as a Pixel 3a, Samsung Galaxy S10e) feel free to use whatever example Android device you would like. 

### This sample app is best for mobile devices

This Sample App Example is best for mobile devices since bound witnessing is optimal on mobile. Integration into your application should be based on a mobile app architecture. 

> For this app we will only use one screen with one activity. 

Please note that the complete code for part one of this guide is in the SamplePartOne folder. We encourage you to go through this tutorial to better understand the process of creating an origin chain utilizing this sdk.

## Create a project 

1. Open Android Studio

2. In the main **Welcome to Android Studio dialog**, click **Start a new Android Studio project**

3. In the **New Project** dialog, give your application a name

4. Go ahead and accept the defaults

5. Check the box that includes Kotlin support

6. Accept default location, then click **Next** 

7. In the Target Android Devices dialog, accept the defaults. Click **Next**

8. Select **Empty Activity**. Click **Next**

9. In the **Customize the Activity dialog**, accept the **defaults**

10. Click **Finish**

Briefly examine the file structure. If you need a refresher, please refer to the aforementioned [Google Introduction](#app-structure-with-kotlin)

We also recommend that you run your app on a physical device (if you have one available) otherwise you could use an emulator. 

There are two gradle scripts in Android Studio that you will need to update. Please note that these are additions and not meant to replace anything generated from Android Studio

build.gradle (Project: YourAppName ) as seen in the android studio project structure 

```gradle
allProjects {
  repositories {
    mavenCentral()
    maven {
      url  "<https://dl.bintray.com/xyoraclenetwork/xyo>"
    }
  }
}
```

build.gradle (Module: app) as seen in the android studio project structure 

```gradle
dependencies {
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0"
    implementation "network.xyo:sdk-core-kotlin:3.0.3"
    implementation "org.bouncycastle:bcpkix-jdk15on:1.58"
}

// you may need to include this 

packagingOptions {
    exclude 'META-INF/atomicfu.kotlin_module'
}
```

## Design 

Since this is a simple app, we won't need to do much design, we are only creating a button to create, sign and display an origin chain hash. 

### Code or design: your choice

If you are an experienced Android developer, you will already have your process. 

`activity_main.xml`

Here is the code you should start up with, which has an empty Constraint Layout

```xml
<?xml version="1.0" encoding="utf-8"?>

<android.support.constraint.ConstraintLayout
        xmlns:android="<http://schemas.android.com/apk/res/android>"
        xmlns:tools="<http://schemas.android.com/tools>"
        xmlns:app="<http://schemas.android.com/apk/res-auto>"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity" 
        android:background="@color/screenBackground">
</android.support.constraint.ConstraintLayout>
```

Here is the code after adding the `TextView` for the hash and the `button` to initiate an origin chain

```xml
<TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="0"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent" 
        app:fontFamily="@font/lato_bold"
        android:textSize="12sp"
        app:layout_constraintVertical_bias="0.3"
        android:textColor="@android:color/white" 
        android:id="@+id/textView"
        app:layout_constraintStart_toStartOf="parent"
        android:layout_marginEnd="32dp"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginRight="32dp" 
        android:layout_marginLeft="32dp"
        android:layout_marginStart="32dp"/>

<Button
        android:text="Create Origin"
        android:background="@color/buttonBackground"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/origin_button"
        app:layout_constraintStart_toEndOf="parent"
        android:layout_marginStart="8dp"
        app:layout_constraintEnd_toStartOf="parent"
        android:layout_marginEnd="8dp" 
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@+id/textView" 
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
**Note** `marginStart` is the same as `marginLeft` and `marginEnd` is the same as `marginRight`

You can create the button through code or through using the design editor by dragging in a `Button` from the `Common` or `Buttons` Palette

## Logic

Once you have the layout set, let's get to creating an origin chain in our simple app. 

We will use under 62 lines of code to do this.

### Set up auto imports

Before we begin, we need to make sure we set up auto imports so Android Studio automatically imports any classes that are needed by the Kotlin code. 

To do this (in Android Studio) 
1. Click on **file** 
2. Go to **other settings**
3. Then go to **preferences for new projects**
4. Then go to **click on other settings** 
5. Then go to **auto import** 
6. Finally, click **Add Unambiguous Imports on the fly**

This will reduce the work you'll have to do on your end as your simple app becomes more robust. 

### MainActivity code 

We will work in the kotlin class `MainAcitivity` which is the activity for our screen. 

To start we want to create a function to create and sign an origin chain
```kotlin
private fun originChainCreator() : XyoOriginChainCreator {

}
```
Why is this private? We only need the mainActivities function that the user initiates with the button tap to be public. This is good practice. 

In our main SDK guide you should have read this section: 

```kotlin
// a key value store to store persist state and bound witnesses
val storage = XyoInMemoryStorageProvider()

// a hash implementation for the node to hash with
val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")

// a place to store all off the blocks that the node makes
val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)

// a place to store all of the origin state (index, keys, previous hash)
val stateRepo = XyoStorageOriginStateRepository(storage)

// the node object to create origin blocks
val node = XyoOriginChainCreator(blockRepo, stateRepo, hasher)

// we will for the purposes of this project change the node object to a creator object, we will define node in the public onCreate function

val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)
```

This will get us ready to get the bound witness so that we can create an origin chain

```kotlin
private fun originChainCreator() : XyoOriginChainCreator {
    val storage = XyoInMemoryStorageProvider()
    val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
    val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)
    val stateRepo = XyoStorageOriginStateRepository(storage)
    val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)

}

```
It's time to add a listener for bound witness to our creator, without a bound witness we can't have an origin chain. This addListener takes two arguments, a key, and a callback for the listener

We bring in a function that will confirm the successful boundwitness and use that bound witness object to get the byte to hex string info that we want to display


```kotlin
creator.addListener("main", object: XyoNodeListener() {
    override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
    super.onBoundWitnessEndSuccess(boundWitness)
    
  }
})
```
In order for us to successfully display an origin chain hash each time we tap the button, we want to avoid blocking any current threads. For this we use the GlobalScope `launch` extension function.

In this function we get our hash from the `boundWitness` that we listened for. 

```kotlin
    GlobalScope.launch {
    val hash = boundWitness.getHash(hasher).await().bytesCopy.toHexString()
    
    }

```
Now to avoid any delays or asynchronous behavior we want to provide the app with an ability to queue up UI actions should the current thread not be UI related. For this we will use the `runOnUiThread` method

```kotlin
  runOnUIThread {
    textView.text = hash
  }
```
Here we are using setting the `textView.text` to the `hash` when the UI is ready for it. You should recognize the `textView` from the `activity_main.xml` file.

Here is where we are at 

```kotlin
private fun originChainCreator() : XyoOriginChainCreator {
        val storage = XyoInMemoryStorageProvider()
        val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
        val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)
        val stateRepo = XyoStorageOriginStateRepository(storage)
        val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)

        creator.addListener("main", object: XyoNodeListener() {
            override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
                super.onBoundWitnessEndSuccess(boundWitness)

                GlobalScope.launch {
                    val hash = boundWitness.getHash(hasher).await().bytesCopy.toHexString()

                runOnUiThread {
                  textView.text = hash
              }
          }
      }
  })
}
```

Lastly we will use the core sdk to create a signer, add the signer to the origin state, and return the origin chain

```kotlin
  val signer = XyoSha256WithSecp256K.newInstance()

  creator.originState.addSigner(signer)

  return creator
```

Our final `originChainCreator()` should look like this

```kotlin
    private fun originChainCreator() : XyoOriginChainCreator {
        val storage = XyoInMemoryStorageProvider()
        val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
        val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)
        val stateRepo = XyoStorageOriginStateRepository(storage)
        val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)

        creator.addListener("main", object: XyoNodeListener() {
            override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
                super.onBoundWitnessEndSuccess(boundWitness)

                GlobalScope.launch {
                    val hash = boundWitness.getHash(hasher).await().bytesCopy.toHexString()

                    runOnUiThread {
                        textView.text = hash
                    }
                }
            }
        })

        val signer = XyoSha256WithSecp256K.newInstance()

        creator.originState.addSigner(signer)

        return creator
    }

```

Now we have a chain that we can assign to our `node` for the create method tied to our button tap. Since we have included `GlobalScope` and `runOnUiThread` to our `addListener` function, we should have no collisons or build issues.

```kotlin
// the node representing our chain for the user
var node = originChainCreator()
```

```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
```

This function is what creates our content view with the current state. Now we only need to add an `setOnClickListener`

```kotlin
\\ use the id from the button

origin_button.setOnClickListener {
  \\ again to avoid blockages
  GlobalScope.launch {
    node.selfSignOriginChain().await()
  }
}
```

We have now set up the node to sign off and display the bound witness hash after tapping the `Origin Chain` button.

Here is the complete MainActivity class

```kotlin
class MainActivity : AppCompatActivity() {
    var node = originChainCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        origin_button.setOnClickListener {
            GlobalScope.launch {
                node.selfSignOriginChain().await()
            }
        }
    }

    private fun originChainCreator() : XyoOriginChainCreator {
        val storage = XyoInMemoryStorageProvider()
        val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
        val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)
        val stateRepo = XyoStorageOriginStateRepository(storage)
        val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)

        creator.addListener("main", object: XyoNodeListener() {
            override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
                super.onBoundWitnessEndSuccess(boundWitness)

                GlobalScope.launch {
                    val hash = boundWitness.getHash(hasher).await().bytesCopy.toHexString()

                    runOnUiThread {
                        textView.text = hash
                    }
                }
            }
        })

        val signer = XyoSha256WithSecp256K.newInstance()

        creator.originState.addSigner(signer)

        return creator
    }

}
```

We have our interactivity set and our layout set. We can now click on the `run` button. 
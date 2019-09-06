# Sample Kotlin Project 

- Android Example

This is a simple integration guide for the XYO Core Kotlin Library. We do recommend experience with Kotlin and Android development but have included guides to get you started. 

[For the source code refer to this link](https://github.com/XYOracleNetwork/sdk-core-kotlin/blob/master/sample/SampleApplication/app/src/main/java/com/example/sampleapplication/MainActivity.kt)

## Table of Contents

-   [Title](#sample-kotlin-project)
-   [App Structure](#app-structure)
-   [Using Android Studio](#using-android-studio)
-   [Create the Project](#create-the-project)
-   [Project Design](#project-design)
-   [Our First Activity](#our-first-activity)
-   [Add Location](#add-location)
-   [Conclusion](#conclusion)


## App Structure 
- Kotlin with Android Studio

[Click here for an introduction from Google](<https://codelabs.developers.google.com/codelabs/build-your-first-android-app-kotlin/index.html?index=..%2F..index#0>)

Versions used in this sample

- Android Studio `3.4.2`
- Java `12.0.1`

## Using Android Studio

We are using Android Studio to create this simple app, make sure it is updated to the latest versions. 

### Virtual Device Emulator 

When creating the configuration for the emulator, we recommend using a mid-range device definition for a wider range of coverage for android devices. (Such as a Pixel 3a, Samsung Galaxy S10e) feel free to use whatever example Android device you would like. 

### This sample app is best for mobile devices

This Sample App Example is best for mobile devices since bound witnessing is optimal on mobile. Integration into your application should be based on a mobile app architecture. 

> For this app we will only use one screen with one activity. 

Please note that the complete code for part one of this guide is in the Sample folder. We encourage you to go through this tutorial to better understand the process of creating an origin chain utilizing this sdk.

## Create the project 

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

build.gradle:  **(Project: YourAppName )** as seen in the android studio project structure 

```gradle
allProjects {
  repositories {
    mavenCentral()
    maven {
      url  "https://dl.bintray.com/xyoraclenetwork/xyo"
    }
  }
}
```

build.gradle **(Module: app)** as seen in the android studio project structure 

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

## Project Design 

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
        android:textSize="12sp"
        app:layout_constraintVertical_bias="0.3"
        android:textColor="@android:color/white" 
        android:id="@+id/bwHashTextView"
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
        app:layout_constraintTop_toBottomOf="@+id/bwHashTextView" 
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
**Note** 

`marginStart` is the same as `marginLeft` and `marginEnd` is the same as `marginRight`

You can bring in your own font with the `TextView` - `app:fontFamily` selector 

You can create the button through code or through using the design editor by dragging in a `Button` from the `Common` or `Buttons` Palette

## Our First Activity

Once you have the layout set, let's get to creating an origin chain in our simple app. 

### Set up auto imports

Before we begin, we need to make sure we set up auto imports so Android Studio automatically imports any classes that are needed by the Kotlin code. 

To do this (in Android Studio) 
1. Click on **file** 
2. Go to **other settings**
3. Then go to **preferences for new projects**
4. Then go to **click on other settings** 
5. Then go to **auto import** 
6. Finally, click **Add Unambiguous Imports on the fly**

This will reduce the work you'll have to do on your end as your simple app needs to include more dependencies. 

### Start with Creating a Bound Witness

We will work in the kotlin class `MainAcitivity.kt` which is the activity for our screen. 

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
```
### Set up the values needed for origin chain

This will get us ready to get the bound witness so that we can create an origin chain

```kotlin
private fun originChainCreator() : XyoOriginChainCreator {
    val storage = XyoInMemoryStorageProvider()
    val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
    val blockRepo = XyoStorageOriginBlockRepository(storage, hasher)
    val stateRepo = XyoStorageOriginStateRepository(storage)
    // a slightt rename for the spcific return that we want. This can be whatever you like
    val creator = XyoOriginChainCreator(blockRepo, stateRepo, hasher)
}
```

### Set up the listener

It's time to add a listener for bound witness to our creator, without a bound witness we can't have an origin chain. This addListener takes two arguments, a key, and a callback for the listener

We bring in a function that will confirm the successful boundwitness and use that bound witness object to get the byte to hex string info that we want to display


```kotlin
creator.addListener("main", object: XyoNodeListener() {
    override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
    super.onBoundWitnessEndSuccess(boundWitness)
    
  }
})
```

In order for us to successfully display an origin chain hash each time we tap the button, we want to avoid blocking any current threads. For this we use the GlobalScope `launch` extension function. This is a coroutine, if you are familiar with Kotlin then you have probably worked with coroutines before. [If not, here is some reading that you can do](https://kotlinlang.org/docs/reference/coroutines-overview.html).  

In this function we get our hash from the `boundWitness` that we listened for. 

```kotlin
    GlobalScope.launch {
    val hash = boundWitness.getHash(hasher).await().bytesCopy.toHexString()
    
    }

```

Now to avoid any delays or asynchronous behavior we want to provide the app with an ability to queue up UI actions should the current thread not be UI related. For this we will use the `runOnUiThread` method in our `GlobalScope` coroutine

```kotlin
  runOnUIThread {
    bwHashTextView.text = hash
  }
```

Here we are using setting the `bwHashTextView.text` to the `hash` when the UI is ready for it. You should recognize the `bwHashTextView` from the `activity_main.xml` file.

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
                  bwHashTextView.text = hash
              }
          }
      }
  })
}
```

We use the core sdk to create a signer, add the signer to the origin state, and return the origin chain

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
                        bwHashTextView.text = hash
                    }
                }
            }
        })

        val signer = XyoSha256WithSecp256K.newInstance()

        creator.originState.addSigner(signer)

        return creator
    }

```

Now we have a chain that we can assign to our `node` for the create method tied to our button tap. Since we have included `GlobalScope` and `runOnUiThread` to our `addListener` function, we should have no collisons.

We add the created node as a local variable in our `MainActivity` class

```kotlin
// the node representing our chain for the user
var node = originChainCreator()
```

This function is what creates our content view with the current state. 

```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
```

Now we only need to add an `setOnClickListener` for our button. We could add an `onClick` selector in our xml, but this is a preferred practice.

```kotlin
// use the id from the button

origin_button.setOnClickListener {
  // again to avoid blockages
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
                        bwHashTextView.text = hash
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

### Run the project

We have our interactivity set and our layout set. We can now click on the `run` button. 

The run button is on the upper right hand of the Android Studio interface, it is a play button next to the edit configurations menu and the build hammer button. 

Once you click on the play button, it will build the application, and if not installed already install the application on your connected Android device or your chosen emulator. 

You should now see the app appear similar to your layout from the design viewer. 

Test the functionality by tapping on `create origin`, when you tap the button you should see a hash appear above in the `textArea`.

Go ahead and tap the button again for a new hash. 

> Congratulations you have now integrated the XYO SDK Core into your Android application. 

## Add Location

Let's keep going. We want to add a heuristic and see what heuristic we are adding. We'll add a GPS location to our bound witness chain.

Let's add another `<TextView>` to the main activity xml

`activity_main.xml`

```kotlin
    <TextView
            android:id="@+id/textViewLatLng"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:layout_marginLeft="8dp"
            android:layout_marginEnd="8dp"
            android:layout_marginRight="8dp"
            android:layout_marginBottom="8dp"
            android:text="@string/_0"
            android:textColor="@android:color/white"
            android:textSize="18sp"
            app:fontFamily="@font/lato_bold"
            app:layout_constraintBottom_toTopOf="@+id/origin_button"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/bwHashTextView" />
```

We set the constraints to ensure that the gps location heuristic that we are adding is between the Create Origin button and the hash text view. 

Double check the app layout. This layout is viewable in the latest android studio. This should be to the right of the text editor in the preview. If not, you can click preview on the right navigation under `Gradle`.

We should be good with our views and button. Now let's add a location. 

### Get permission

Before we add a location heuristic we are going to need permission from the user to access the location on a smartphone. We can also use this best practice if we are working on a device simulator.

Since this is a simple application we should not spend too much time or code on permissions (if you are working to integrate XYO into a production ready app, make sure to have a more robust permission handler than what is presented here)

This is based on the `requestPermissions` method from the `ActivityCompat` class for activity features. 

This will check permissions before the sample app runs for the first time. This will go in the `onCreate()` method.

```kotlin
val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

ActivityCompat.requestPermissions(this, permissions, 0)
```


We also need to update the android manifest xml file with the permission:

```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

```
This permission element should be in the root and not in the `<application>` element.

Your manifest should look like this:

```xml
<?xml version="1.0" encoding="utf-8"?>

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.example.sampleapplication">

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

    <application
            android:allowBackup="true"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <meta-data
                android:name="preloaded_fonts"
                android:resource="@array/preloaded_fonts"/>
    </application>

</manifest>

```

So, we have cleared the permissions hurdle, now let's add the location heuristic. 

Again in our main readme, you should see an example of adding a heuristic that looks like this:

This is a pseudo code for what we need to add a heuristic

```kotlin
node.addHeuristic("MyHeuristic", object : XyoHeuristicGetter {
	// will get called right before the bound witness starts
	override fun getHeuristic(): XyoBuff? {
	    if (conditionIsMet()) {
	    	// object will be put into the bound witness
				return getMyHeuristic()
	    }

        // object will not be put into the bound witness 
        return null
    }
})
```

What we want to do is add the `heuristic` to the node in the `OnCreate()` method, this heuristic will be added right before the chain is signed.

```kotlin
node.addHeuristic("GPS", <someHeuristicResolver>)
```

Let's get the heuristic to add

```kotlin
// the gpsHeuristicResolver is what will be plugged in to the addHeuristic method
    private val gpsHeuristicResolver = object : XyoHeuristicGetter {

        override fun getHeuristic(): XyoIterableStructure? {
            // bring in the location manager
            val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // check if we have permission
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                // with permission we get the last known location from the location manager
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null) {
                    // as long as we have a location, we take it and convert it for inclusion in the Origin Chain
                    val encodedLat = ByteBuffer.allocate(8).putDouble(lastLocation.latitude).array()
                    val encodedLng = ByteBuffer.allocate(8).putDouble(lastLocation.longitude).array()
                    val lat = XyoObjectStructure.newInstance(XyoSchemas.LAT, encodedLat)
                    val lng = XyoObjectStructure.newInstance(XyoSchemas.LNG, encodedLng)
                    // let the user see the GPS in a readable format
                    runOnUiThread {
                        textViewLatLng.text = lastLocation.latitude.toString() + ", " + lastLocation.longitude.toString()
                    }

                    return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.GPS, arrayOf(lat, lng))
                }
                // if we don't have permission
            } else {
                runOnUiThread {
                    textViewLatLng.text = "no gps permission"
                }
            }
            return null
        }
    }
}

```

Here we check that we have the permission to get the location, and if we have the permission, we get the last known location if there is one, then we encode the location coordinates before we create a new XyoObjectStructure for the heuristic to be compatible with our origin chain object. 

We also add a `runOnUiThread` to print out the non-encoded location coordinates so that the user can see a human readable version of the location that is added to the origin chain. 

We also added another text response if the permission was not obtained `no gps permission`

Once we have the resolver set, we can plug it into the `addHeuristic` method

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  ...
  node.addHeuristic("GPS", gpsHeuristicResolver)
}
```

The `onCreate()` method should look like this when we are done

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)

        node.addHeuristic("GPS", gpsHeuristicResolver)

        origin_button.setOnClickListener {
            GlobalScope.launch {
                node.selfSignOriginChain().await()
            }
        }
    }

```

Now rebuild and run the app. 

When you tap or click (in a simulator) the `create origin` button, you should see the GPS coordinate appear right before the hash. 

## Conclusion
You have now created an origin chain and added the gps heuristic to the origin chain. 

Made with 🔥and ❄️ by [XY - The Persistent Company](https://www.xy.company)

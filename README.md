[logo]: https://cdn.xy.company/img/brand/XYO_full_colored.png

[![logo]](https://xyo.network)

# sdk-core-kotlin

![](https://github.com/XYOracleNetwork/client-xyo-nodejs/workflows/CI/badge.svg?branch=develop) [![Download](https://api.bintray.com/packages/xyoraclenetwork/xyo/sdk-core-kotlin/images/download.svg)](https://bintray.com/xyoraclenetwork/xyo/sdk-core-kotlin/_latestVersion) [![BCH compliance](https://bettercodehub.com/edge/badge/XYOracleNetwork/sdk-core-kotlin?branch=master)](https://bettercodehub.com/) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/2fb2eb69c1db455299ffce57b0216aa6)](https://www.codacy.com/app/XYOracleNetwork/sdk-core-kotlin?utm_source=github.com&utm_medium=referral&utm_content=XYOracleNetwork/sdk-core-kotlin&utm_campaign=Badge_Grade) [![Maintainability](https://api.codeclimate.com/v1/badges/af641257b27ecea22a9f/maintainability)](https://codeclimate.com/github/XYOracleNetwork/sdk-core-kotlin/maintainability) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=XYOracleNetwork_sdk-core-kotlin&metric=alert_status)](https://sonarcloud.io/dashboard?id=XYOracleNetwork_sdk-core-kotlin) [![Known Vulnerabilities](https://snyk.io/test/github/XYOracleNetwork/sdk-core-kotlin/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/XYOracleNetwork/sdk-core-kotlin?targetFile=build.gradle)


## Table of Contents

-   [Title](#sdk-core-kotlin)
-   [Description](#description)
-   [Long Description](#long-description)
-   [XYO Origin Block Protocol](#xyo-origin-block-protocol)
-   [Getting Started](#getting-started)
-   [Install](#install)
-   [Building and Testing with Gradle](#building-and-testing-with-gradle)
-   [Origin Chain](#origin-chain)
-   [Bound Witness](#bound-witness)
-   [Node Listener](#node-listener)
-   [Testing](#testing)
-   [Maintainers](#maintainers)
-   [Contributing](#contributing)
-   [License](#license)
-   [Credits](#credits)

This `README.md` document is an overview of the common methods that you may need when integrating the XYO Core SDK into your project. 

For an easy to use entry integration guide, take a look at our [Sample App Guide](https://github.com/XYOracleNetwork/sdk-core-kotlin/blob/master/sample/README.md)

## Description

Library to preform all core XYO Network functions which includes

-   Creating an origin chain
-   Maintaining an origin chain
-   Negotiations for talking to other nodes
-   Other basic functionality

## Long Description

A library to preform all core XYO Network functions.
This includes creating an origin chain, maintaining an origin chain, negotiations for talking to other nodes, and other basic functionality.
The library has heavily abstracted modules so that all operations will work with any crypto, storage, networking, etc.

## Read the Yellow Paper

The XYO protocol for creating origin-blocks is specified in the [XYO Yellow Paper](https://docs.xyo.network/XYO-Yellow-Paper.pdf). In it, it describes the behavior of how a node on the XYO network should create Bound Witnesses. Note, the behavior is not coupled with any particular technology constraints around transport layers, cryptographic algorithms, or hashing algorithms.

## Getting Started

## Install

You can add sdk-core-kotlin to your existing app by cloning the project and manually adding it to your build.gradle or by using JitPack.

### Build From Source

1) Clone from github

    `git clone git@github.com:XYOracleNetwork/sdk-core-kotlin.git`

2) Add project to settings.gradle

```gradle
    include ':sdk-core-kotlin'
    project(':sdk-core-kotlin').projectDir = new File('../sdk-core-kotlin')
```

3) Include in project

```gradle
    implementation project (':sdk-core-kotlin')
```

### Using JitPack

#### With Gradle

1. Point maven to `https://jitpack.io`

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. Include sdk-core-kotlin in dependencies

 ```gradle
 dependencies {
	implementation 'com.github.XYOracleNetwork:sdk-core-kotlin:v3.0.36'
 }
 ```

### With Maven

1. Point maven to `https://jitpack.io`

```maven
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

2. Include sdk-core-kotlin in dependencies

 ```maven
 <dependency>
    <groupId>com.github.XYOracleNetwork</groupId>
    <artifactId>sdk-core-kotlin</artifactId>
    <version>Tag</version>
 </dependency>
 ```

## Building and Testing with Gradle

**Building**
Source is located in /src/main/\*

    gradle build

**You should start by setting up an interface to this library through creating an origin chain creator object.**

## Origin Chain

-   Through an origin chain creator object one can create and maintain an origin chain. 

```kotlin
val originChain = XyoOriginChainCreator(blockRepo, stateRepo, hash)
```

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

After creating a node, it is standard to add a signer, and create a genesis block.

```kotlin
// creates a signer with a random private key
val signer = XyoSha256WithSecp256K.newInstance()
    
// adds the signer to the node
node.originState.addSigner(signer: signer)

// creates a origin block with itself (genesis block if this is the first block you make)
node.selfSignOriginChain()
```

After creating a genesis block, your origin chain has officially started. Remember, all of the state is stored in the state repository (`XyoOriginChainStateRepository`) and the block repository (`XyoOriginBlockRepository`) that are constructed with the node. 

Both repositories are very high level and can be implemented for one's needs. Out of the box, this library comes with an implementation for key value store databases (`XyoStorageOriginBlockRepository`) and (`XyoStorageOriginChainStateRepository`). 

The `XyoStorageProvider` interface defines the methods for a simple key value store. There is a default implementation of an in memory key value store that comes with this library (`XyoInMemoryStorage`).

### Creating Origin Blocks

After a node has been created, it can be used to create origin blocks with other nodes. The process of talking to other nodes has been abstracted through use of a pipe (e.g. tcp, ble, memory) that handles all of the transport logic. This interface is defined as `XyoNetworkPipe`. This library ships with a and a tcp client and server pipe.

#### Using a TCP Pipe

**Client**

```kotlin
// creates a socket with the peer
val socket = Socket("myarchivist.com", 11000)

// creates a pipe so that we can send formatted data through the socket
val pipe = XyoTcpPipe(socket, null)

// create a handler so that we can do the starting handshake with the node
val handler = XyoNetworkHandler(pipe)

// create the bound witness with the node on the socket
val newBoundWitness = node.boundWitness(handler, testProcedureCatalogue).await()
```

**Server**

```kotlin
// create a tcp server on port 11000
val server = XyoTcpServer(11000)

// listen from the server for connection events
server.listen { pipe ->
	// put bound witness into new thread (optional)
	GlobalScope.launch {
	
				// create a handler so that we can do the starting handshake with the node
	    	val handler = XyoNetworkHandler(pipe)
		
				// do the bound witness with the node
	    	val newBoundWitness = nodeTwo.boundWitness(handler, XyoBoundWitnessCatalog).await()
	}
}
```

> Further examples of interacting through a socket can be found [here](https://github.com/XYOracleNetwork/sdk-core-kotlin/blob/feature/getting-started/src/test/kotlin/network/xyo/sdkcorekotlin/node/interaction/XyoStandardInteractionTest.kt).

## Bound Witness

### Adding Custom Data to a Bound Witness

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

## Node Listener

### Adding a Listener to a Node

```kotlin
node.addListener("MyListener", object : XyoNodeListener {
	override fun onBoundWitnessDiscovered(boundWitness: XyoBoundWitness) {
		// will get called when a new bound witness if found
	}

	override fun onBoundWitnessEndFailure(error: Exception?) {
		// will get called when a bound witness errors out
	}

	override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
		// will get called when a bound witness is completed
	}

	override fun onBoundWitnessStart() {
		// will get called when a bound witness starts
	} 
})
```

## Testing

All tests can be found in /src/test/\*

    gradle test

## Maintainers

-   Carter Harrison

## License

See the [LICENSE.md](LICENSE) file for license details.

## Credits

Made with 🔥and ❄️ by [XYO](https://www.xyo.network)

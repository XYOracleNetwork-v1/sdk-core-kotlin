package com.example.sampleapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.hashing.XyoBasicHashBase
import network.xyo.sdkcorekotlin.node.XyoNodeListener
import network.xyo.sdkcorekotlin.node.XyoOriginChainCreator
import network.xyo.sdkcorekotlin.persist.XyoInMemoryStorageProvider
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginBlockRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.toHexString


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



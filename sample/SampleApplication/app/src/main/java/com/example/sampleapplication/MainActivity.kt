package com.example.sampleapplication

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.hashing.XyoBasicHashBase
import network.xyo.sdkcorekotlin.heuristics.XyoHeuristicGetter
import network.xyo.sdkcorekotlin.node.XyoNodeListener
import network.xyo.sdkcorekotlin.node.XyoOriginChainCreator
import network.xyo.sdkcorekotlin.persist.XyoInMemoryStorageProvider
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginBlockRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import network.xyo.sdkobjectmodelkotlin.toHexString
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    private val node = originChainCreator()

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

    private val gpsHeuristicResolver = object : XyoHeuristicGetter {

        override fun getHeuristic(): XyoIterableStructure? {
            val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if (lastLocation != null) {
                    val encodedLat = ByteBuffer.allocate(8).putDouble(lastLocation.latitude).array()
                    val encodedLng = ByteBuffer.allocate(8).putDouble(lastLocation.longitude).array()
                    val lat = XyoObjectStructure.newInstance(XyoSchemas.LAT, encodedLat)
                    val lng = XyoObjectStructure.newInstance(XyoSchemas.LNG, encodedLng)

                    runOnUiThread {
                        textViewLatLng.text = lastLocation.latitude.toString() + ", " + lastLocation.longitude.toString()
                    }

                    return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.GPS, arrayOf(lat, lng))
                }
            } else {
                runOnUiThread {
                    textViewLatLng.text = "no gps permission"
                }
            }

            return null
        }
    }
}



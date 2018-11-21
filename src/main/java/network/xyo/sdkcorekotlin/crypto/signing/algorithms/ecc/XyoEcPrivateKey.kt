package network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECParameterSpec


abstract class XyoEcPrivateKey(private val ecSpec: ECParameterSpec) : ECPrivateKey, XyoInterpreter {

    override fun getAlgorithm(): String {
        return "EC"
    }

    override fun getEncoded(): ByteArray {
        return s.toByteArray()
    }

    override fun getFormat(): String {
        return "XyoEcPrivateKey"
    }

    override fun getParams(): ECParameterSpec {
        return  ecSpec
    }

    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, s.toByteArray())

    override val schema: XyoObjectSchema
        get() = XyoSchemas.EC_PRIVATE_KEY


    companion object {

        fun getInstance(byteArray: ByteArray, ecSpec: ECParameterSpec): XyoEcPrivateKey {
            return object : XyoEcPrivateKey(ecSpec) {
                override fun getS(): BigInteger {
                    return BigInteger(XyoObjectCreator.getObjectValue(byteArray))
                }

                override val self: ByteArray
                    get() = byteArray
            }
        }
    }
}
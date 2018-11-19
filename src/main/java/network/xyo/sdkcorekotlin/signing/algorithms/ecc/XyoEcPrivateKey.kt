package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECParameterSpec

/**
 * A class for encoding EC Private keys.
 *
 * @param s the private key.
 * @param ecSpec the Java ECParameterSpec to understand the private key.
 * @major 0x0a
 * @minor 0x0a
 */
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

    @ExperimentalUnsignedTypes
    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, s.toByteArray())

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.EC_PRIVATE_KEY


    companion object {

        @ExperimentalUnsignedTypes
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
package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import java.math.BigInteger
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECParameterSpec

/**
 * A class for encoding EC Private keys.
 *
 * @param s the private key.
 * @param ecSpec the Java ECParameterSpec to understand the private key.
 * @major 0x0a
 * @minor 0x02
 */
class XyoEcPrivateKey(private val s : BigInteger,
                      private val ecSpec: ECParameterSpec) : ECPrivateKey, XyoObject() {

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

    override fun getS(): BigInteger {
        return s
    }

    override val id: ByteArray
        get() = byteArrayOf(major, major)

    override val objectInBytes: ByteArray
        get() = encoded

    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize


    companion object : XyoObjectProvider() {
        override val major: Byte
            get() = 0x0a

        override val minor: Byte
            get() = 0x02

        override val sizeOfBytesToGetSize: Int?
            get() = 2

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val sSize = XyoUnsignedHelper.readUnsignedShort(reader.read(0, 2))
            val encodedS = reader.read(2, sSize - 2)
            return XyoEcPrivateKey(BigInteger(encodedS), XyoSecp256K1UnCompressedPublicKey.ecPramSpec)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedShort(byteArray)
        }
    }
}
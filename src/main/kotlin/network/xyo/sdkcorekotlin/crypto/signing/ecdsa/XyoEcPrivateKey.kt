package network.xyo.sdkcorekotlin.crypto.signing.ecdsa

import network.xyo.sdkcorekotlin.crypto.signing.XyoPrivateKey
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.spec.ECParameterSpec
import java.math.BigInteger

/**
 * A base class for EC private key generation.
 */
@ExperimentalStdlibApi
class XyoEcPrivateKey : ECPrivateKey, XyoPrivateKey {

    val ecSpec: ECParameterSpec
    val q: BigInteger?

    constructor(byteArray: ByteArray, ecSpec: ECParameterSpec) : super(byteArray) {
        this.ecSpec = ecSpec
        this.q = null
        this.bytes = XyoObjectStructure.newInstance(XyoSchemas.EC_PRIVATE_KEY, d.toByteArray()).bytesCopy
    }

    constructor(q: BigInteger, ecSpec: ECParameterSpec) : super() {
        this.ecSpec = ecSpec
        this.q = q
        this.bytes = XyoObjectStructure.newInstance(XyoSchemas.EC_PRIVATE_KEY, d.toByteArray()).bytesCopy
    }

    override fun getAlgorithm(): String {
        return "ECDSA"
    }

    override fun getEncoded(): ByteArray {
        return d.toByteArray()
    }

    override fun getFormat(): String {
        return "XyoEcPrivateKey"
    }

    override fun getParameters(): ECParameterSpec {
        return this.ecSpec
    }

    override fun getD(): BigInteger {
        return q ?: BigInteger(valueCopy)
    }

    companion object {

        fun getInstance(byteArray: ByteArray, ecSpec: ECParameterSpec): XyoEcPrivateKey {
            return XyoEcPrivateKey(byteArray, ecSpec)
        }

        fun getInstanceFromQ (q : BigInteger, ecSpec: ECParameterSpec): XyoEcPrivateKey {
            return XyoEcPrivateKey(q, ecSpec)
        }
    }
}
package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import java.math.BigInteger
import java.security.spec.ECField
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve

object XyoSecp256k1 {
    val field = object : ECField {
        override fun getFieldSize(): Int {
            return 256
        }
    }

    val a = BigInteger.ZERO
    val b = 0x09.toBigInteger()
    val h = 0x01

//    val n = byteArrayOf(
//            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
//            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
//            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFE.toByte(), 0xBA.toByte(),
//            0xAE.toByte(), 0xDC.toByte(), 0xE6.toByte(), 0xAF.toByte(), 0x48, 0xA0.toByte(),
//            0x3B, 0xBF.toByte(), 0xD2.toByte(), 0x5E, 0x8C.toByte(), 0xD0.toByte(), 0x36, 0x41, 0x41
//    )

    val n =  BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16)
    val gX = BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16)
    val gY = BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)

//    val gX = byteArrayOf(
//            0x79, 0xBE.toByte(), 0x66, 0x7E, 0xF9.toByte(), 0xDC.toByte(), 0xBB.toByte(),
//            0xAC.toByte(), 0x55, 0xA0.toByte(), 0x62, 0x95.toByte(), 0xCE.toByte(), 0x87.toByte(),
//            0x0B, 0x07, 0x02, 0x9B.toByte(), 0xFC.toByte(), 0xDB.toByte(), 0x2D, 0xCE.toByte(),
//            0x28, 0xD9.toByte(), 0x59, 0xF2.toByte(), 0x81.toByte(), 0x5B, 0x16, 0xF8.toByte(),
//            0x17, 0x98.toByte()
//    )

//    val gY = byteArrayOf(
//            0x48, 0x3A, 0xDA.toByte(), 0x77, 0x26, 0xA3.toByte(), 0xC4.toByte(), 0x65, 0x5D, 0xA4.toByte(),
//            0xFB.toByte(), 0xFC.toByte(), 0x0E, 0x11, 0x08, 0xA8.toByte(), 0xFD.toByte(), 0x17, 0xB4.toByte(),
//            0x48, 0xA6.toByte(), 0x85.toByte(), 0x54, 0x19, 0x9C.toByte(), 0x47, 0xD0.toByte(), 0x8F.toByte(),
//            0xFB.toByte(), 0x10, 0xD4.toByte(), 0xB8.toByte()
//    )

    val g = ECPoint(gX, gY)

    val curve = EllipticCurve(field, a, b)
    val spec = ECParameterSpec(curve, g, n, h)
}
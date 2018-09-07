package network.xyo.sdkcorekotlin.data

/**
 * A base class for typing objects in a major minor scheme.
 */
abstract class XyoType {
    /**
     * The major of the object.
     */
    abstract val major : Byte

    /**
     * The minor of the object.
     */
    abstract val minor : Byte

    /**
     * The id of the object. Major then minor.
     */
    val id : ByteArray
        get() = byteArrayOf(major, minor)
}
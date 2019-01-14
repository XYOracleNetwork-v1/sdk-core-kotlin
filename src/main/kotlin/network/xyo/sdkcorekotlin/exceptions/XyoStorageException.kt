package network.xyo.sdkcorekotlin.exceptions

/**
 * An exception for the StorageProviderInterface. Can throw during writing, reading, deleting, and other storage
 * related operations.
 *
 * @property message The message describing the storage exception/
 */
class XyoStorageException (override val message: String?) : XyoException()
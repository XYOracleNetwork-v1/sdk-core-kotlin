package network.xyo.sdkcorekotlin.persist

import network.xyo.sdkcorekotlin.XyoException

/**
 * An exception for the StorageProviderInterface. Can throw during writing, reading, deleting, and other persist
 * related operations.
 *
 * @property message The message describing the persist exception/
 */
class XyoStorageException (override val message: String?) : XyoException()
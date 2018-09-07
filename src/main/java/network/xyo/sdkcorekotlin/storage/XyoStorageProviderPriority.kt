package network.xyo.sdkcorekotlin.storage

/**
 * The class for describing the priority when writing to storage.
 */
enum class XyoStorageProviderPriority {
    /**
     * Used when the write to storage can be slow speed.
     */
    PRIORITY_LOW,

    /**
     * Used when the write to storage must be medium speed.
     */
    PRIORITY_MED,

    /**
     * Used when the write to storage must be high speed.
     */
    PRIORITY_HIGH
}
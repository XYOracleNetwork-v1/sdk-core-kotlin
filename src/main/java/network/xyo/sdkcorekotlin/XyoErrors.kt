package network.xyo.sdkcorekotlin

/**
 * All of the error codes at a XyoError object can contain.
 */
enum class XyoErrors {
    /**
     * Catastrophic failure.
     */
    ERR_CRITICAL,

    /**
     * Returned when the core can't get the ID.
     */
    ERR_NOID,

    /**
     *  Returned when the payload in inaccessible.
     */
    ERR_CANT_GET_PAYLOAD,

    /**
     *  Returned when the core can't create a signer.
     */
    ERR_NOSIGNER,

    /**
     *  Could not bind to address provided.
     */
    ERR_ADDRESS_UNAVAILABLE,

    /**
     * / Core network services are unavailable.
     */
    ERR_NETWORK_UNAVAILABLE,

    /**
     * Returned when The receiver refused connection.
     */
    ERR_RECEIVER_REFUSED_CONNECTION,

    /**
     *  Returned when a core service is busy.
     */
    ERR_BUSY,

    /**
     *  Returned by when no keypair has been generated.
     */
    ERR_NOKEYS,

    /**
     * Returned if objectInBytes is malformed e.g. too big.
     */
    ERR_BADDATA,

    /**
     *  Returned if the public key is invalid.
     */
    ERR_BADPUBKEY,

    /**
     *  Returned if the signature encoding is improper.
     */
    ERR_BADSIG,

    /**
     *  Returned if objectInBytes is improperly encrypted.
     */
    ERR_CORRUPTDATA,

    /**
     *  Returned if can't insert because key is already mapped.
     */
    ERR_KEY_ALREADY_EXISTS,

    /**
     *  Returned if there wasn't enough memory to store.
     */
    ERR_INSUFFICIENT_MEMORY,

    /**
     * Returned if there was a hardware error.
     */
    ERR_INTERNAL_ERROR,

    /**
     *  Returned if the disk timed out on read/write.
     */
    ERR_TIMEOUT,

    /**
     *  Returned if delete failed.
     */
    ERR_COULD_NOT_DELETE,

    /**
     * Returned if permissions are improper.
     */
    ERR_PERMISSION,

    /**
     *  Returned if key isn't found in map.
     */
    ERR_KEY_DOES_NOT_EXIST,

    /**
     *  Returned if a party disconnected during a connection.
     */
    ERR_DISCONNECT

};
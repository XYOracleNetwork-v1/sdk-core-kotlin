package network.xyo.sdkcorekotlin

enum class XyoErrors {
    ERR_CRITICAL, // Catastrophic failure.
    ERR_NOID, // Returned when the core can't get the ID.
    ERR_CANT_GET_PAYLOAD, // Returned when the payload in inaccessible.
    ERR_NOSIGNER, // Returned when the core can't create a signer.
    ERR_ADDRESS_UNAVAILABLE, // Could not bind to address provided.
    ERR_NETWORK_UNAVAILABLE, // Core network services are unavailable.
    ERR_RECEIVER_REFUSED_CONNECTION, // Returned when The receiver refused connection.
    ERR_BUSY, // Returned when a core service is busy.
    ERR_NOKEYS, // Returned by when no keypair has been generated.
    ERR_BADDATA, // Returned if data is malformed e.g. too big.
    ERR_BADPUBKEY, // Returned if the public key is invalid.
    ERR_BADSIG, // Returned if the signature encoding is improper.
    ERR_CORRUPTDATA, // Returned if data is improperly encrypted.
    ERR_KEY_ALREADY_EXISTS, // Returned if can't insert because key is already mapped.
    ERR_INSUFFICIENT_MEMORY, // Returned if there wasn't enough memory to store.
    ERR_INTERNAL_ERROR, // Returned if there was a hardware error.
    ERR_TIMEOUT, // Returned if the disk timed out on read/write.
    ERR_COULD_NOT_DELETE, // Returned if delete failed.
    ERR_PERMISSION, // Returned if permissions are improper.
    ERR_KEY_DOES_NOT_EXIST // Returned if key isn't found in map.
};
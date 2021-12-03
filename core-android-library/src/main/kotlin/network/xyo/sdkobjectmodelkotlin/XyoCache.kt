package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import java.lang.ref.WeakReference
// import javax.xml.bind.DatatypeConverter

/**
 * A simple caching file that uses weak references to cache any item.
 *
 * @type T Is the type of the object wishing to cache, this also is the the type of the XyoCacheAble interface.
 * @property itemToCache The XyoCacheAble interface to get a FRESH item, to cache.
 */
class XyoCache<T> (private val itemToCache : XyoCacheAble<T>) {
    private var reference : WeakReference<T>? = null

    /**
     * The cache-able interface to cache any item.
     *
     * @type iT The type that the get get function returns. (The type of the item to cache)
     */
    interface XyoCacheAble<iT> {

        /**
         * Gets a clean instance of the object.
         *
         * @return iT The clean (not cached) item.
         */
        fun get() : iT
    }

    /**
     * Gets the item from the cache, if not in the cache it item will be freshly collected and cached.
     *
     * @return The cached or clean item of type T.
     */
    fun get () : T {
        val cachedItem = reference?.get()

        if (cachedItem != null) {
            return cachedItem
        }

        val newItem = itemToCache.get()
        reference = WeakReference(newItem)
        return newItem
    }

    /**
     * Clears the cache, the next time the this.get() function is called, the cache will be updated.
     */
    fun clear () {
        reference = null
    }
}
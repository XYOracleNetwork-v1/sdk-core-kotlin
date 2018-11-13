package network.xyo.sdkcorekotlin.storage.rocksdb

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import org.rocksdb.Options
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException

class XyoRocksDbStorage (path: String) : XyoStorageProviderInterface {
    private val db : RocksDB = getDb(path)

    override fun containsKey(key: ByteArray): Deferred<Boolean> = GlobalScope.async {
        return@async db.get(key) != null
    }

    override fun delete(key: ByteArray): Deferred<Exception?> = GlobalScope.async {
        try {
            db.remove(key)
            return@async null
        } catch (e : RocksDBException) {
            return@async e
        }
    }

    override fun getAllKeys(): Deferred<Array<ByteArray>> {
        throw Exception("Stub")
    }

    override fun read(key: ByteArray): Deferred<ByteArray?> = GlobalScope.async {
        try {
            return@async db.get(key)
        } catch (e : RocksDBException) {
            return@async null
        }
    }

    override fun write(key: ByteArray, value: ByteArray): Deferred<Exception?> = GlobalScope.async {
        try {
            db.put(key, value)
            return@async null
        } catch (e : RocksDBException) {
            return@async e
        }
    }

    private fun getDb (path : String) : RocksDB {
        try {
            RocksDB.loadLibrary()
            val options = Options().setCreateIfMissing(true)
            return RocksDB.open(options, path)
        } catch (e : RocksDBException) {
            throw e
        }
    }
}
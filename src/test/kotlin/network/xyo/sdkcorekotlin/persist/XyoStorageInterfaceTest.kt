package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import org.junit.Assert
import org.junit.Test

class XyoStorageInterfaceTest : XyoTestBase() {

    private fun testStorageInterface (storageProviderInterface: XyoKeyValueStore) = runBlocking {
        val keyOfItemOne = byteArrayOf(0x00)
        val valueOfItemTwo = byteArrayOf(0x13, 37)

        storageProviderInterface.write(keyOfItemOne, valueOfItemTwo)

        val readWhatWrote = storageProviderInterface.read(keyOfItemOne)
        Assert.assertArrayEquals(valueOfItemTwo, readWhatWrote)
        Assert.assertTrue(storageProviderInterface.containsKey(keyOfItemOne))

        val allKeys = storageProviderInterface.getAllKeys()

        for (item in allKeys) {
            Assert.assertArrayEquals(keyOfItemOne, item)
        }

        storageProviderInterface.delete(keyOfItemOne)
        val readWhatWroteIfDeleted = storageProviderInterface.read(keyOfItemOne)
        Assert.assertArrayEquals(null, readWhatWroteIfDeleted)
        Assert.assertFalse(storageProviderInterface.containsKey(keyOfItemOne))
    }

    @Test
    fun testInMemoryStorage () {
        testStorageInterface(XyoInMemoryStorageProvider())
    }

    @Test
    fun testWeakReferenceCaching () {
        testStorageInterface(XyoWeakReferenceCaching(XyoInMemoryStorageProvider()))
    }
}
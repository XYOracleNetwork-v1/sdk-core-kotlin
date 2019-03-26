package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import org.junit.Assert
import org.junit.Test

class XyoStorageInterfaceTest : XyoTestBase() {

    private fun testStorageInterface (storageProviderInterface: XyoStorageProvider) = runBlocking {
        val keyOfItemOne = byteArrayOf(0x00)
        val valueOfItemTwo = byteArrayOf(0x13, 37)

        storageProviderInterface.write(keyOfItemOne, valueOfItemTwo).await()

        val readWhatWrote = storageProviderInterface.read(keyOfItemOne).await()
        Assert.assertArrayEquals(valueOfItemTwo, readWhatWrote)
        Assert.assertTrue(storageProviderInterface.containsKey(keyOfItemOne).await())

        val allKeys = storageProviderInterface.getAllKeys().await()

        for (item in allKeys) {
            Assert.assertArrayEquals(keyOfItemOne, item)
        }

        storageProviderInterface.delete(keyOfItemOne).await()
        val readWhatWroteIfDeleted = storageProviderInterface.read(keyOfItemOne).await()
        Assert.assertArrayEquals(null, readWhatWroteIfDeleted)
        Assert.assertFalse(storageProviderInterface.containsKey(keyOfItemOne).await())
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
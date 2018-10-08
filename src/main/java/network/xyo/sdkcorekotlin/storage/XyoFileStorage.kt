package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import java.io.*
import java.nio.charset.Charset
import java.util.*


/**
 * A simple XyoStorageProviderInterface implementation to store files on the naive file system
 * using file names as keys.
 *
 * @param basePath The base path for storing files.
 */
class XyoFileStorage(private val basePath : File) : XyoStorageProviderInterface {
    override fun containsKey(key: ByteArray): Deferred<Boolean> = GlobalScope.async {
        val file = File(basePath, encodeFileName(key)).absoluteFile
        return@async file.exists()
    }

    override fun delete(key: ByteArray): Deferred<Exception?> = GlobalScope.async {
        try {
            File(basePath, encodeFileName(key)).delete()
            return@async null
        } catch (exception : Exception) {
            return@async exception
        }
    }

    override fun getAllKeys(): Deferred<Array<ByteArray>> = GlobalScope.async {
        val listOfFiles = basePath.listFiles()
        return@async Array(listOfFiles.size) { i -> decodeFileName(listOfFiles[i].name)}
    }

    override fun read(key: ByteArray): Deferred<ByteArray?>  = GlobalScope.async {
        try {
            val inputFile = File(basePath, encodeFileName(key))
            return@async readFileFromDisk(inputFile).await()
        } catch (ioException : IOException) {
            return@async null
        }
    }


    override fun write(key: ByteArray, value: ByteArray): Deferred<Exception?> = GlobalScope.async {
        try {
            val file = File(basePath, encodeFileName(key))
            writeFileToDisk(file, value).await()
            return@async null
        } catch (ioException : IOException) {
            return@async ioException
        }
    }

    private fun encodeFileName (key : ByteArray) : String {
        return Base64.getUrlEncoder().encode(key).toString(Charset.defaultCharset())
    }

    private fun decodeFileName (name : String) : ByteArray {
        return Base64.getUrlDecoder().decode(name)
    }

    private fun writeFileToDisk (file : File, value : ByteArray) = GlobalScope.async {
        val pathField = FileOutputStream(file)
        pathField.write(value)
        pathField.flush()
        pathField.close()
        return@async null
    }

    private fun readFileFromDisk (file : File) = GlobalScope.async {
        val data = ByteArray(file.length().toInt())
        val inputStream = FileInputStream(file)
        inputStream.read(data, 0, data.size)
        inputStream.close()
        return@async data
    }

    init {
        basePath.mkdirs()
    }
}


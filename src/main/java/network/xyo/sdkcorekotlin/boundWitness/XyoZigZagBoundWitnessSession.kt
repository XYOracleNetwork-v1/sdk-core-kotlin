package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.exceptions.XyoBoundWitnessCreationException
import network.xyo.sdkcorekotlin.exceptions.XyoException
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectExceotion
import java.nio.ByteBuffer


class XyoZigZagBoundWitnessSession(private val pipe : XyoNetworkPipe,
                                   payload : ByteArray,
                                   signers : Array<XyoSigner>,
                                   private val choice : ByteArray) : XyoZigZagBoundWitness(signers, payload) {

    private var cycles = 0

    suspend fun doBoundWitness(transfer: ByteArray?) : Exception?  {
        try {
            if (!completed) {
                val response = sendAndReceive(transfer != null, transfer).await()

                if (cycles == 0 && transfer != null && response != null) {
                    incomingData(response, false).await()
                } else {
                    cycles++
                    return doBoundWitness(response)
                }
            }

            return null
        } catch (exception : XyoException) {
            return exception
        } catch (exception : XyoObjectExceotion) {
            return exception
        }
    }

    private fun sendAndReceive (didHaveData : Boolean, transfer : ByteArray?) = GlobalScope.async {
        val response : ByteArray?
        val returnData = incomingData(transfer, cycles == 0 && didHaveData).await()

        if (cycles == 0 && !didHaveData) {
            val buffer = ByteBuffer.allocate(1 + choice.size + returnData.size)
            buffer.put(choice.size.toByte())
            buffer.put(choice)
            buffer.put(returnData)
            response = pipe.send(buffer.array(), true).await() ?: throw XyoBoundWitnessCreationException("Response is null!")
        } else {
            response = pipe.send(returnData, cycles == 0).await()

            if (cycles == 0 && response == null) {
                throw XyoBoundWitnessCreationException("Response is null!")
            }
        }

        return@async response
    }
}
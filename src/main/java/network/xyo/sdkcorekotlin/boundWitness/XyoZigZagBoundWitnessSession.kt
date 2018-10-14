package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import network.xyo.sdkcorekotlin.exceptions.XyoNoObjectException
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.signing.XyoSigner

class XyoZigZagBoundWitnessSession(private val pipe : XyoNetworkPipe,
                                   payload : XyoPayload,
                                   signers : Array<XyoSigner>,
                                   private val choice : ByteArray) : XyoZigZagBoundWitness(signers, payload) {

    private var cycles = 0

    suspend fun doBoundWitness(data: ByteArray?) : Exception?  {
        try {
            if (!completed) {
                var transfer : XyoBoundWitnessTransfer? = null

                if (data != null) {
                    transfer = XyoBoundWitnessTransfer.createFromPacked(data) as XyoBoundWitnessTransfer
                }

                val response = sendAndRecive(data != null, transfer).await()

                if (cycles == 0 && data != null && response != null) {
                    val inComingTransfer = XyoBoundWitnessTransfer.createFromPacked(response) as XyoBoundWitnessTransfer
                    incomingData(inComingTransfer, false).await()
                } else {
                    cycles++
                    return doBoundWitness(response)
                }
            }

            return null
        } catch (corruptDataException : XyoCorruptDataException) {

            corruptDataException.printStackTrace()
            return corruptDataException

        } catch (noObjectException : XyoNoObjectException) {

            noObjectException.printStackTrace()
            return noObjectException

        } catch (boundWitnessException : XyoBoundWitnessCreationException) {

            boundWitnessException.printStackTrace()
            return boundWitnessException

        }
    }

    private fun sendAndRecive (didHaveData : Boolean, transfer : XyoBoundWitnessTransfer?) = GlobalScope.async {
        val response : ByteArray?
        val returnData = incomingData(transfer, cycles == 0 && didHaveData).await()
        val returnDataEncoded = returnData.untyped

        if (cycles == 0 && !didHaveData) {
            val merger = XyoByteArraySetter(3)
            merger.add(XyoUnsignedHelper.createUnsignedByte(choice.size), 0)
            merger.add(choice, 1)
            merger.add(returnDataEncoded, 2)

            response = pipe.send(merger.merge(), true).await() ?: throw XyoBoundWitnessCreationException("Response is null!")
        } else {
            response = pipe.send(returnDataEncoded, cycles == 0).await()

            if (cycles == 0 && response == null) {
                throw XyoBoundWitnessCreationException("Response is null!")
            }
        }

        return@async response
    }
}
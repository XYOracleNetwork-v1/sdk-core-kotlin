package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.signing.XyoSigner

class XyoZigZagBoundWitnessSession(private val pipe : XyoNetworkPipe,
                                   payload : XyoPayload,
                                   signers : Array<XyoSigner>,
                                   private val choice : ByteArray) : XyoZigZagBoundWitness(signers, payload) {

    private var cycles = 0

    override fun doBoundWitness(data: ByteArray?): Deferred<XyoError?> {
        return async {
            if (!completed) {
                var transferValue : XyoBoundWitnessTransfer? = null

                if (data != null) {
                    val transfer = XyoBoundWitnessTransfer.createFromPacked(data)
                    transferValue = transfer.value as? XyoBoundWitnessTransfer ?: return@async transfer.error
                    if (transfer.error != null) return@async transfer.error
                }

                val returnData = incomingData(transferValue, cycles == 0 && data != null).await()
                val returnDataValue = returnData.value ?: return@async returnData.error
                if (returnData.error != null) return@async returnData.error

                val bytesToSendBack = returnDataValue.untyped
                val bytesToSendBackValue = bytesToSendBack.value ?: return@async bytesToSendBack.error
                if (bytesToSendBack.error != null) return@async bytesToSendBack.error

                val responseValue : ByteArray

                if (cycles == 0  && data == null) {
                    val merger = XyoByteArraySetter(2)
                    merger.add(choice, 0)
                    merger.add(bytesToSendBackValue, 1)
                    val response = pipe.send(merger.merge()).await()
                    responseValue = response.value ?: return@async response.error
                    if (response.error != null) return@async response.error
                } else {
                    val response = pipe.send(bytesToSendBackValue).await()
                    responseValue = response.value ?: return@async response.error
                    if (response.error != null) return@async response.error
                }

                if (cycles == 0 && data != null) {
                    val transfer = XyoBoundWitnessTransfer.createFromPacked(responseValue)
                    transferValue = transfer.value as? XyoBoundWitnessTransfer ?: return@async transfer.error
                    if (transfer.error != null) return@async transfer.error
                    incomingData(transferValue, false).await()
                } else {
                    cycles++
                    return@async doBoundWitness(responseValue).await()
                }
            }

            return@async null
        }
    }
}
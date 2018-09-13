package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.signing.XyoSigner

class XyoZigZagBoundWitnessSession(private val pipe : XyoNetworkPipe,
                                   payload : XyoPayload,
                                   signers : Array<XyoSigner>,
                                   private val choice : ByteArray) : XyoZigZagBoundWitness(signers, payload) {

    private var cycles = 0

    suspend fun doBoundWitness(data: ByteArray?) {
        if (!completed) {
            var transfer : XyoBoundWitnessTransfer? = null

            if (data != null) {
                transfer = XyoBoundWitnessTransfer.createFromPacked(data) as XyoBoundWitnessTransfer
            }

            val returnData = incomingData(transfer, cycles == 0 && data != null).await()
            val returnDataEncoded = returnData.untyped
            val response : ByteArray

            if (cycles == 0  && data == null) {
                val merger = XyoByteArraySetter(2)
                merger.add(choice, 0)
                merger.add(returnDataEncoded, 1)
                response = pipe.send(merger.merge()).await() ?: return
            } else {
                response = pipe.send(returnDataEncoded).await() ?: return
            }

            if (cycles == 0 && data != null) {
                val inComingTransfer = XyoBoundWitnessTransfer.createFromPacked(response) as XyoBoundWitnessTransfer
                incomingData(inComingTransfer, false).await()
            } else {
                cycles++
                doBoundWitness(response)
            }
        }
    }
}
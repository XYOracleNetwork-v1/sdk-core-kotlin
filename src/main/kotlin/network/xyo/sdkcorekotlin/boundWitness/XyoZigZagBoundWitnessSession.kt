package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.XyoException
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.network.XyoNetworkHandler
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject


class XyoZigZagBoundWitnessSession(private val handler : XyoNetworkHandler,
                                   signedPayload : Array<XyoBuff>,
                                   unsignedPayload : Array<XyoBuff>,
                                   signers : Array<XyoSigner>,
                                   private val choice : ByteArray) : XyoZigZagBoundWitness(signers, signedPayload, unsignedPayload) {

    private var cycles = 0

    suspend fun doBoundWitness(transfer: XyoIterableObject?) : Exception? {
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
        } catch (exception: XyoException) {
            XyoLog.logError("Bound witness creation error: $exception", TAG, exception)
            return exception
        } catch (exception: XyoObjectException) {
            XyoLog.logError("Bound witness creation error: $exception", TAG, exception)
            return exception
        }
    }

    private fun sendAndReceive (didHaveData : Boolean, transfer : XyoIterableObject?) = GlobalScope.async {
        val response : ByteArray?
        val returnData = incomingData(transfer, cycles == 0 && didHaveData).await()

        if (cycles == 0 && !didHaveData) {
            response = handler.sendChoicePacket(choice, returnData.bytesCopy).await() ?: throw XyoBoundWitnessCreationException("Response is null!")
        } else {
            response = handler.pipe.send(returnData.bytesCopy, cycles == 0).await()
            if (cycles == 0 && response == null) throw XyoBoundWitnessCreationException("Response is null!")
        }

        return@async if (response == null) {
                null
            } else {
                object : XyoIterableObject() {
                    override val allowedOffset: Int = 0
                    override var item: ByteArray = response
                }
            }
    }

    companion object {
        const val TAG = "BWS"
    }
}
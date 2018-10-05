package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.Deferred
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

                val returnData = incomingData(transfer, cycles == 0 && data != null).await()
                val returnDataEncoded = returnData.untyped
                val response : ByteArray

                if (cycles == 0 && data == null) {
                    val merger = XyoByteArraySetter(3)
                    merger.add(XyoUnsignedHelper.createUnsignedByte(choice.size), 0)
                    merger.add(choice, 1)
                    merger.add(returnDataEncoded, 2)

                    response = pipe.send(merger.merge(), true).await() ?: throw XyoBoundWitnessCreationException("Response is null!")
                } else {
                    val responseFromReturnDataEncode = pipe.send(returnDataEncoded, cycles == 0).await()

                    if (cycles == 0 && responseFromReturnDataEncode == null) {
                        throw XyoBoundWitnessCreationException("Response is null!")
                    }

                    response = responseFromReturnDataEncode ?: return null
                }

                if (cycles == 0 && data != null) {
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

        } catch (exception : Exception) {

            exception.printStackTrace()
            throw exception

        }
    }
}
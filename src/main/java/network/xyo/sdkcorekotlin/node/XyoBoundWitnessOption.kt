package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.data.XyoObject

/**
 * A base class for bound witness options. For example a sentinel would have an option for
 * bridging.
 */
abstract class XyoBoundWitnessOption {
    /**
     * The flag of the option.
     */
    abstract val flag : Int

    /**
     * Gets the signed data to include in the bound witness.
     *
     * @return The optional signed data.
     */
    abstract fun getSignedPayload() : XyoObject?

    /**
     * Gets the unsigned payload to include in the bound witness.
     *
     * @return The option unsigned data.
     */
    abstract fun getUnsignedPayload() : XyoObject?

    /**
     * Adds the current option to the mapping, so it can be used.
     */
    fun enable () {
        boundWitnessOptions[flag] = this
    }

    /**
     * Disables the current option.
     */
    fun disbale () {
        boundWitnessOptions.remove(flag)
    }

    companion object {
        private val boundWitnessOptions = HashMap<Int, XyoBoundWitnessOption>()

        /**
         * Gets all of the unsigned payloads for a given flag.
         *
         * @param bitFlag The flag to filter.
         * @return All of the options that comply to that filter.
         */
        fun getUnSignedPayloads (bitFlag : Int) : ArrayList<XyoObject> {
            val unsignedPayloads = ArrayList<XyoObject>()

            for ((flag, option) in boundWitnessOptions) {
                if (flag and bitFlag != 0) {
                    val unsignedPayload = option.getUnsignedPayload()

                    if (unsignedPayload != null) {
                        unsignedPayloads.add(unsignedPayload)
                    }
                }
            }

            return unsignedPayloads
        }

        /**
         * Gets all of the signed payloads for a given flag.
         *
         * @param bitFlag The flag to filter.
         * @return All of the options that comply to that filter.
         */
        fun getSignedPayloads (bitFlag: Int) : ArrayList<XyoObject> {
            val signedPayloads = ArrayList<XyoObject>()

            for ((flag, option) in boundWitnessOptions) {
                if (flag and bitFlag != 0) {
                    val signedPayload = option.getSignedPayload()

                    if (signedPayload != null) {
                        signedPayloads.add(signedPayload)
                    }
                }
            }

            return signedPayloads
        }
    }
}
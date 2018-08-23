package network.xyo.sdkcorekotlin.exceptions

class ExceededNumberOfPartiesException (field: String, maxSize: Int) : Exception() {
    override val message: String = "Exceeded Number of Parties! Field $field, maxSize: $maxSize"
}
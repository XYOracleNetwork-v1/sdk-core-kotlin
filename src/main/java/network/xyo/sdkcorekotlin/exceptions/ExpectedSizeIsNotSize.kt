package network.xyo.sdkcorekotlin.exceptions

class ExpectedSizeIsNotSize (expectedSize : Int, actualSize : Int) : RuntimeException() {
    override val message: String? = "Error Packing Item! expectedSize: $expectedSize, actualSize $actualSize"
}
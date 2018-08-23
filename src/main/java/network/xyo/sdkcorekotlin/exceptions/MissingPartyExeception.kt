package network.xyo.sdkcorekotlin.exceptions

class MissingPartyExeception (expected : Int, actual : Int) : Exception() {
    override val message: String = "Missing Party: expected: $expected, actual, $actual"
}
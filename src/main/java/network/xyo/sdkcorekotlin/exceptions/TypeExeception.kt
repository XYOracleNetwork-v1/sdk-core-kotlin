package network.xyo.sdkcorekotlin.exceptions

class TypeExeception (expectedMajor : Byte, expectedMinor : Byte, actualMajor: Byte, actualMinor: Byte) : RuntimeException() {
    override val message: String? = "TypeExeception! Types do not align! expectedMajor: $expectedMajor, expectedMinor: $expectedMinor, actualMajor: $actualMajor, actualMinor: $actualMinor"
}
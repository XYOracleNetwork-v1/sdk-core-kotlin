package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import org.junit.Assert
import org.junit.Test

class XyoBoundWitnessVerifyTest : XyoTestBase() {

    @Test
    fun testBoundWitnessVerifyWithVerifyKnown () {
        runBlocking {
            XyoSha256WithSecp256K.enable()

            val boundWitnessBytes = "6002012B201547201944000C414C577D06AAB023090EAAB4E3CD9C2BC84D5DCE70C3496B598F6A7D309D3C54E0CEDE66BB9290EF2ED0F6E4BB3A78C9415685673216DB59C1CD96A21EAF7EDB9F201547201944000C41DDFC13C84EC7AA7E5CB75F472E9471CC3E3974EC060681BCE3D1EFD350F603428CC98BD51A7D2F8F281E51B04BA0B0EE2D6CAE39D554AC57630062D1ACDC257B201749201A46000943207A38E8253C2478AFCFF2FAE445B4F958BCB9801354CF335BD66441EF903C2DA32021838B7F6C96F8E184F1F453423CE6181D536127AC6B3E4099B586E5CC91219220174A201A4700094420732501D3CA3BFD9FD0197F35AD53C6E5F18F72A84D6DBC2D18AEDF33BE41351A210086AE6A12D97A7979324884C5ACEDEA677936F46870933789E09385850239CDA7".hexStringToByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes)
            val verify = XyoBoundWitnessVerify(false)

            Assert.assertTrue(verify.verify(createdBoundWitness).await() ?: false)
        }
    }

    @Test
    fun testBoundWitnessVerifyWithVerifyUnknown () {
        runBlocking {
            val boundWitnessBytes = "6002012B201547201944000C414C577D06AAB023090EAAB4E3CD9C2BC84D5DCE70C3496B598F6A7D309D3C54E0CEDE66BB9290EF2ED0F6E4BB3A78C9415685673216DB59C1CD96A21EAF7EDB9F201547201944000C41DDFC13C84EC7AA7E5CB75F472E9471CC3E3974EC060681BCE3D1EFD350F603428CC98BD51A7D2F8F281E51B04BA0B0EE2D6CAE39D554AC57630062D1ACDC257B201749201A46000943207A38E8253C2478AFCFF2FAE445B4F958BCB9801354CF335BD66441EF903C2DA32021838B7F6C96F8E184F1F453423CE6181D536127AC6B3E4099B586E5CC91219220174A201A4700094420732501D3CA3BFD9FD0197F35AD53C6E5F18F72A84D6DBC2D18AEDF33BE41351A210086AE6A12D97A7979324884C5ACEDEA677936F46870933789E09385850239CDA7".hexStringToByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes)
            val verify = XyoBoundWitnessVerify(false)

            Assert.assertFalse(verify.verify(createdBoundWitness).await() ?: false)
        }
    }

    @Test
    fun testBoundWitnessVerifyWithVerifyUnknownButAllow () {
        runBlocking {
            val boundWitnessBytes = "6002012B201547201944000C414C577D06AAB023090EAAB4E3CD9C2BC84D5DCE70C3496B598F6A7D309D3C54E0CEDE66BB9290EF2ED0F6E4BB3A78C9415685673216DB59C1CD96A21EAF7EDB9F201547201944000C41DDFC13C84EC7AA7E5CB75F472E9471CC3E3974EC060681BCE3D1EFD350F603428CC98BD51A7D2F8F281E51B04BA0B0EE2D6CAE39D554AC57630062D1ACDC257B201749201A46000943207A38E8253C2478AFCFF2FAE445B4F958BCB9801354CF335BD66441EF903C2DA32021838B7F6C96F8E184F1F453423CE6181D536127AC6B3E4099B586E5CC91219220174A201A4700094420732501D3CA3BFD9FD0197F35AD53C6E5F18F72A84D6DBC2D18AEDF33BE41351A210086AE6A12D97A7979324884C5ACEDEA677936F46870933789E09385850239CDA7".hexStringToByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes)
            val verify = XyoBoundWitnessVerify(true)

            Assert.assertTrue(verify.verify(createdBoundWitness).await() ?: false)
        }
    }

    @Test
    fun testBoundWitnessVerifyWithBadSignature () {
        runBlocking {
            XyoSha256WithSecp256K.enable()

            val boundWitnessBytes = "6002012B201547201944000C414C517D06AAB023090EAAA4E3CD9C2BC84D5DCE70C3496B598F6A7D309D3C54E0CEDE66BB9290EF2ED0F6E4BB3A78C9415685673216DB59C1CD96A21EAF7EDB9F201547201944000C41DDFC13C84EC7AA7E5CB75F472E9471CC3E3974EC060681BCE3D1EFD350F603428CC98BD51A7D2F8F281E51B04BA0B0EE2D6CAE39D554AC57630062D1ACDC257B201749201A46000943207A38E8253C2478AFCFFFFAE445B4F958BCB9801354CF335BD66441EF903C2DA32021838B7F6C96F8E184F1F453423CE6181D536127AC6B3E4099B586E5CC91219220174A201A4700094420732501D3CA3BFD9FD0197F35AD53C6E5F18F72A84D6DBC2D18AEDF33BE41353A210086AE6A12D97A7979324884C5ACEDEA677736F46870933789E09385850239CDA7".hexStringToByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes)
            val verify = XyoBoundWitnessVerify(false)

            Assert.assertFalse(verify.verify(createdBoundWitness).await() ?: true)
        }
    }
}
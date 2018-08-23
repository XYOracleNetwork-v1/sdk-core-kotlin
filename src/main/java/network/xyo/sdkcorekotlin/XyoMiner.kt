package network.xyo.sdkcorekotlin

class XyoMiner {
//    private var isMining = false
//    fun createMiningJob (data: ByteArray, minDifficulty : Int) : Deferred<XYOBinaryItem> {
//        val merger = XYOByteArraySetter(2)
//         return async(CommonPool){
//            isMining = true
//            while (isMining) {
//                val seed = ByteArray(16)
//                Random().nextBytes(seed)
//
//                merger.add(seed, 0)
//                merger.add(data, 1)
//
//                val hash = MessageDigest.getInstance("SHA-256").digest(merger.merge())
//                val difficulty = getDifficultyOfHash(hash)
//                if (difficulty >= minDifficulty) {
//                    isMining = false
//                    return@async XYOBinaryItem.fromData(seed)
//                }
//            }
//             isMining = false
//            return@async XYOBinaryItem.fromData(byteArrayOf(0x00))
//        }
//    }
//
//    fun getDifficultyOfHash (hash : ByteArray) : Int {
//        var totalDifficulty = 0x00
//        for (byte in hash){
//            if (byte == 0x00.toByte()){
//                totalDifficulty += 0xff
//            } else {
//                totalDifficulty += (0xff - (byte.toInt() and 0xff))
//                break
//            }
//        }
//        return totalDifficulty
//    }
}
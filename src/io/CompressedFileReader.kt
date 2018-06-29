package io

import java.io.FileInputStream
import java.io.File


class CompressedFileReader(file:File){
    val inputStream = FileInputStream(file)

    var buffer : ByteArray

    var binaryString = ""

    var index=0

    init {
        buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
    }

    fun size() = buffer.size

    private fun byteToString(it: Byte): String {
        return String.format("%8s",Integer.toBinaryString(0xff and(it.toInt()))).replace(' ','0')
    }

    fun read() : Char? {
        if (binaryString.isEmpty() && index!=size()){
            buffer[index].apply { binaryString = byteToString(this) }
            index++
        }
        if (index == size()-1){

        }
        if (binaryString.isEmpty()){
            return null
        }
        val ret = binaryString[0]
        binaryString = binaryString.substring(1)
        return ret
    }
}
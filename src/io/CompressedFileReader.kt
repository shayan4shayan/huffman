package io

import java.io.FileInputStream
import java.io.File
import javax.print.attribute.IntegerSyntax


class CompressedFileReader(file: File) {
    val inputStream = FileInputStream(file)

    var buffer: ByteArray

    var binaryString = ""

    var index = 0

    init {
        buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
    }

    fun size() = buffer.size

    private fun byteToString(it: Byte): String {
        return String.format("%8s", Integer.toBinaryString(0xff and (it.toInt()))).replace(' ', '0')
    }

    fun read(): Char? {
        if (binaryString.isEmpty() && index != size()) {
            buffer[index].apply { binaryString = byteToString(this) }
            index++
        }
        if (index == size() - 1) {

        }
        if (binaryString.isEmpty()) {
            return null
        }
        val ret = binaryString[0]
        binaryString = binaryString.substring(1)
        return ret
    }

    fun readTable(eof: Char, seperator: Char): IntArray {
        var i = 0
        val arr = IntArray(256)
        var char = buffer[index].toChar()
        print(index)
        var str = ""
        while (char != eof) {
            if (char != seperator) {
                str += char
            } else {
                arr[i] = if (str.isEmpty()) 0 else str.toInt()
                str = ""
                i++
            }
            index++
            char = buffer[index].toChar()
        }
        arr[i] = if (str.isEmpty()) 0 else str.toInt()

        index++

        return arr
    }
}
package io

import java.io.File
import java.io.FileOutputStream

class FileWriter(path: File) {
    val outputStream = FileOutputStream(path)

    var lastBinary = ""

    fun write(b: String) {
        lastBinary += b
        if (lastBinary.length >= 1000) {
            flush()
        }
    }

    fun flush() {
        while (lastBinary.length >= 8) {
            val item = lastBinary.substring(0, 8)
            val char = Integer.parseInt(item, 2)
            outputStream.write(char)
            lastBinary = lastBinary.substring(8, lastBinary.length)
        }
        outputStream.flush()
    }

    fun flushLast() {
        flush()
        if (lastBinary.isNotEmpty()) {
            //adding zero to first of binary to make sure binary stays the same and 8bit is filled
            var tmp = ""
            val sizeOfZero = 8 - lastBinary.length
            val ch = lastBinary[lastBinary.length-1]
            (0..sizeOfZero).forEach { tmp = if (ch=='1') "${tmp}0" else "${tmp}1" }
            val item = "$lastBinary$tmp"
            val char = Integer.parseInt(item, 2)
            outputStream.write(char)
        }
        outputStream.flush()
    }

    fun close() {
        outputStream.close()
    }
}
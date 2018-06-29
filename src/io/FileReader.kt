package io

import java.io.FileInputStream

class FileReader(file: String) {
    var inputStream: FileInputStream = FileInputStream(file)

    fun read(): Char? {
        return if (inputStream.available() > 0) {
            inputStream.read().toChar()
        } else {
            null
        }
    }
}
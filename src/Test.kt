import java.io.File
import java.io.FileInputStream

/**
 * main function of application
 */
fun main(args: Array<String>) {
    //path is set by default
    val file = File("/home/shayan4shayan/algorithm/")
    val list = ArrayList<File>()
    loadFiles(file, list)
    list.forEach { Huffman(it).handleNewText() }
}

/**
 * reads content of a file and return it as String
 */
fun read(file: File): String {
    val inputStream = FileInputStream(file)
    val bytes = ByteArray(inputStream.available())
    inputStream.read(bytes)
    return String(bytes)
}

/**
 * load all files in a folder recursively into a list
 *
 * @param file is a directory or a file
 * @param list to insert files to it
 */
fun loadFiles(file: File, list: ArrayList<File>) {
    if (file.isDirectory) {
        file.listFiles().forEach { loadFiles(it, list) }
    } else {
        list.add(file)
    }
}

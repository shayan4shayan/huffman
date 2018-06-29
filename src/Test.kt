import io.MyChar
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
    val file = File("/home/shayan4shayan/algorithm/")
    val list = ArrayList<File>()
    loadFiles(file, list)
//    val charList = ArrayList<MyChar>()
//    loadCharList(list, charList)
//    list.forEach { Huffman(it).handleNewText() }
    Huffman(list[0]).handleNewText()
}


fun loadCharList(list: ArrayList<File>, charList: ArrayList<MyChar>) {
    var size = 0
    list.forEach {
        val readed = read(it)
        readed.forEach { charList.find { char -> it == char.char }?.increament() ?: charList.add(MyChar(it)) }
        size += readed.length
    }
    charList.forEach { it.size = size }
}

fun read(file: File): String {
    val inputStream = FileInputStream(file)
    val bytes = ByteArray(inputStream.available())
    inputStream.read(bytes)
    return String(bytes)
}

fun loadFiles(file: File, list: ArrayList<File>) {
    if (file.isDirectory) {
        file.listFiles().forEach { loadFiles(it, list) }
    } else {
        list.add(file)
    }
}

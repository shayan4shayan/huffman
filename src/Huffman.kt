import io.CompressedFileReader
import java.io.File
import java.io.FileOutputStream
import java.util.*

class Huffman(val file: File) {

    private val compressedFile = File(file.absolutePath + ".hmc")
    private val decompressedFile = File(file.absolutePath + ".decoded.txt")

    init {
        if (!compressedFile.exists())
            compressedFile.createNewFile()
        if (decompressedFile.exists())
            decompressedFile.createNewFile()
        println(file.name)
    }


    private var nodes = PriorityQueue<Node> { o1, o2 -> if (o1.value < o2.value) -1 else 1 }
    private var codes = TreeMap<Char, String>()

    private var text = ""
    private var ASCII = IntArray(128)
    private var EOF: Char = ' '

    fun handleNewText(): Boolean {
        text = read(file)
        ASCII = IntArray(256)
        nodes.clear()
        codes.clear()

        calculateCharIntervals(nodes, false)
        for (i in 0 until ASCII.size) {
            if (ASCII[i] == 0) {
                EOF = i.toChar()
                ASCII[i]++;
                break
            }
        }
        nodes.add(Node((1 / text.length).toDouble(), EOF.toString()))
        buildTree(nodes)
        generateCodes(nodes.peek(), "", 0)

        printCodes()
        encodeText()
        decodeText()
        val similarity = FileCompariator(file, decompressedFile).compare()
        println("compress ratio is : ${(1 - (compressedFile.length().toDouble() / file.length().toDouble())) * 100}")
        println("File is ${similarity * 100} percent same to decompressed file")
        println()
        return false


    }

    fun decodeText() {
        val reader = CompressedFileReader(compressedFile)
        val writer = FileOutputStream(decompressedFile)
        var char = reader.read()
        while (char != null) {
            var tmpNode: Node? = nodes.peek()
            while (tmpNode!!.left != null && tmpNode.right != null && char != null) {
                //print(char)
                tmpNode = if (char == '1')
                    tmpNode.right
                else
                    tmpNode.left
                char = reader.read()
            }
            if (tmpNode.character.length == 1) {
                if (tmpNode.character == EOF.toString()) return
                writer.write(tmpNode.character[0].toInt())
            }
        }
    }

    fun encodeText() {
        val outputStream = io.FileWriter(compressedFile)
        for (i in 0 until text.length) {
            outputStream.write(codes[text[i]]!!)
        }
        outputStream.write(codes[EOF]!!)
        outputStream.flushLast()
        outputStream.close()
    }

    fun buildTree(vector: PriorityQueue<Node>) {
        while (vector.size > 1)
            vector.add(Node(vector.poll(), vector.poll()))
    }

    fun printCodes() {
        codes.keys.forEach { println("$it : ${codes[it]}") }
    }

    fun calculateCharIntervals(vector: PriorityQueue<Node>, printIntervals: Boolean) {

        for (i in 0 until text.length)
            ASCII[text[i].toInt()]++

        for (i in ASCII.indices)
            if (ASCII[i] > 0) {
                vector.add(Node(ASCII[i] / (text.length * 1.0), Character.toChars(i)[0].toString()))
                if (printIntervals)
                    println("'" + i.toChar() + "' : " + ASCII[i] / (text.length * 1.0))
            }
    }

    fun generateCodes(node: Node?, s: String, lastIndex: Int) {
        if (node != null) {
            if (node.right != null)
                generateCodes(node.right, s + "1", lastIndex)

            if (node.left != null)
                generateCodes(node.left, s + "0", lastIndex)

            if (node.left == null && node.right == null)
                codes[node.character[0]] = s
        }
    }
}
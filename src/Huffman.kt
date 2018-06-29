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
    private var EOF: Char = Character.toChars(3)[0]
    private var tableEOF = '~'
    var seperator = ','


    fun handleNewText(): Boolean {
        text = read(file)
        ASCII = IntArray(256)
        nodes.clear()
        codes.clear()

        //getting frequency of each character
        for (i in 0 until text.length)
            ASCII[text[i].toInt()]++
        //generating huffman tree
        prepareForEncode()
        //print for log
        printCodes()

        //start encode
        encodeText()
        //clear nodes and table just for simulate decoding
        nodes.clear()
        codes.clear()
        //start decode
        decodeText()
        //compute similarity of files
        val similarity = FileCompariator(file, decompressedFile).compare()
        //print answers
        println("compress ratio is : ${(1 - (compressedFile.length().toDouble() / file.length().toDouble())) * 100}")
        println("File is ${similarity * 100} percent same to decompressed file")
        println()

        return false


    }

    private fun prepareForEncode() {

        calculateCharIntervals(nodes, false)

        nodes.add(Node((1 / text.length).toDouble(), EOF.toString()))
        buildTree(nodes)
        generateCodes(nodes.peek(), "", 0)
    }

    fun decodeText() {
        val reader = CompressedFileReader(compressedFile)
        val writer = FileOutputStream(decompressedFile)
        //loading huffman words frequency from file
        val newArr = reader.readTable(tableEOF, seperator)
        print(Arrays.equals(ASCII, newArr))
        ASCII = newArr
        //generating huffman tree from data loaded from file

        prepareForEncode()

        //start decode
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
        val table = generateTableString()
        //print(table + " " + table.length)
        outputStream.normalWrite(table)
        for (i in 0 until text.length) {
            outputStream.write(codes[text[i]]!!)
        }
        outputStream.write(codes[EOF]!!)
        outputStream.flushLast()
        outputStream.close()
    }

    private fun generateTableString(): String {
        var ret = ""
        if (ASCII[0] > 0) {
            ret += ASCII[0]
        }
        (1 until ASCII.size).forEach {
            ret += seperator
            if (ASCII[it] > 0) {
                ret += ASCII[it]
            }
        }
        ret += tableEOF
        return ret
    }

    fun buildTree(vector: PriorityQueue<Node>) {
        while (vector.size > 1)
            vector.add(Node(vector.poll(), vector.poll()))
    }

    fun printCodes() {
        codes.keys.forEach { println("$it : ${codes[it]}") }
    }

    fun calculateCharIntervals(vector: PriorityQueue<Node>, printIntervals: Boolean) {

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
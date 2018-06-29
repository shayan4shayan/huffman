package io

class MyChar(val char: Char) {
    var frequency = 0

    fun increament() {
        frequency++
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MyChar -> char == other.char
            else -> false
        }
    }

    override fun toString(): String {
        return "char= $char - frequency= $frequency - percent= ${frequency.toFloat() / (size.toFloat() - 1127003) * 100}"
    }

    var size = 1
}
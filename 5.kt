fun main() {
    val q = readln().toInt()
    val stack = Stack()
    for (i in 1..q) {
        val query = readln().split(' ')
        when (query[0]) {
            "push" -> stack.push(query[1].toInt())
            "pop" -> stack.pop()
            "max" -> println(stack.max())
        }
    }
}

class Stack (
    private val stack: MutableList<Pair<Int, Int>> = mutableListOf()
) {
    fun push(v: Int) {
        if (stack.isEmpty())
            stack.addLast(Pair(v, v))
        else
            stack.addLast(Pair(v, maxOf(v, stack.last().second)))
    }

    fun pop() {
        stack.removeLast()
    }

    fun max(): Int {
        return stack.last().second
    }
}
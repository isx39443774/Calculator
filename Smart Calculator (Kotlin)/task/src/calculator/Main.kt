package calculator
import java.math.BigInteger
class SmartCalculator {
    val variables = mutableMapOf<String, String> ()
    fun calculate(list: MutableList<String>){
        val stack = mutableListOf<String>()
        while (list.isNotEmpty()) {
            val ch = list.removeFirst()
            if (ch in "+-*/^") {
                val b = BigInteger(stack.removeLast())
                val a = BigInteger(if (stack.isNotEmpty()) stack.removeLast() else if (ch in "+-") "0" else "1")
                stack.add(when (ch) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> a / b
                    "^" -> a.pow(b.toInt())
                    else -> throw Exception()
                }.toString())
            } else stack.add(ch)
        }
        println(stack.last())
    }
    fun toPostfix(line: String): MutableList<String> {
        val values = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3, "(" to 0, ")" to 0)
        val exp = "${line})".reversed().map { it.toString() }.toMutableList()
        val stack = mutableListOf("(")
        val postfix = mutableListOf<String>()
        while (exp.isNotEmpty()) {
            var ch = exp.removeLast()
            if (ch !in values.keys) {
                while (exp.isNotEmpty() && exp.last() !in values.keys) ch += exp.removeLast()
                postfix.add(variables[ch] ?: ch)
            } else if (ch == ")") {
                while (stack.isNotEmpty() && stack.last() != "(") postfix.add(stack.removeLast())
                stack.removeLast()
            } else {
                while (ch != "(" && stack.isNotEmpty() && values[stack.last()]!!  >= values[ch]!!) postfix.add(stack.removeLast())
                stack.add(ch)
            }
        }
        return postfix
    }
    fun run() {
        while(true) {
            var x = readln().filter { it != ' ' }
            val rep = mapOf("--" to "+", "+-" to "-", "++" to "+")
            for ((k, v) in rep) while(k in x) x = x.replace(k, v)
            when {
                x == "/exit" -> break
                x == "" -> continue
                x == "/help" -> println("The program calculates the sum of numbers")
                x[0] == '/' -> println("Unknown command")
                "=" in x -> {
                    val y = x.split("=").toMutableList()
                    if (Regex("[a-zA-Z]+").matches(y[0])) {
                        y[1] = variables[y[1]] ?: y[1]
                        if (Regex("[+-]?[0-9]+").matches(y[1]) && y.size == 2) {
                            variables[y[0]] = y[1]
                        } else println("Invalid assignment")
                    } else println("Invalid identifier")
                }
                else -> {
                    if (x.count { it == '(' } != x.count { it == ')' } || Regex("[*^/]{2,}").find(x) != null) println("Invalid expression")
                    else {
                        val postfix = toPostfix(x)
                        if (postfix.any { Regex("[a-zA-Z]+").matches(it) }) println("Unknown variable")
                        else calculate(postfix)
                    }
                }
            }
        }
        println("Bye!")
    }
}

fun main() {
    val calculator = SmartCalculator()
    calculator.run()
}
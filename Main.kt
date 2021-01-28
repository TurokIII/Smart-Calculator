package calculator
import java.util.Scanner
import kotlin.math.abs

fun main() {
    getInput()
}

fun getInput() {
    val scan = Scanner(System.`in`)
    while (true) {
        val input = scan.nextLine()
        if (input == "/exit") break else parseInputForAction(input)


    }
    println("Bye!")
}

fun parseInputForAction(input: String) {
    when {
        input == "/help" -> println("The program calculates the sum of numbers")
        input == "" -> {}
        input[0] == '/' -> println("Unknown command")
        else -> {
            try {
                val sum = calculateResult(input)
                println(sum)
            } catch (e: Exception) {
                println("Invalid expression")
            }
        }
    }
}

fun calculateResult(input: String): Int {
    var total = 0
    val cleanInput = cleanInput(input)
    val numbers = parseNumbers(cleanInput)
    val operators = parseOperators(cleanInput)

    val delta = abs(numbers.size - operators.size)
    if (delta != 1) throw Exception()

    for (i in numbers.indices) {
        if (i == 0) {
            total += numbers[0].toInt()
        } else {
            val operator = operators[i - 1]
            if (operator == "+") total += numbers[i].toInt() else total -= numbers[i].toInt()
        }
    }

    return total
}

fun cleanInput(input: String): String {
    var result = input
    while (result.contains("++")) { result = result.replace("++", "+") }
    while (result.contains("--")) { result = result.replace("--", "+") }
    while (result.contains("-+")) { result = result.replace("-+", "-") }
    while (result.contains("+-")) { result = result.replace("+-", "-") }
    while (result.contains("++")) { result = result.replace("++", "+") }

    return result.replace(" ", "")
}

fun parseNumbers(input: String):Array<String> {
    var numbers = ""
    var numString = ""

    for (i in input.indices) {
        val char = input[i]
        if (i == 0 ) {
            if (char == '-') {
                numString += char
                continue
            }
            if (char == '+') {
                continue
            }
        }
        if (char.isDigit()) {
            numString += char
        } else {
            numbers += " $numString"
            numString = ""
        }
    }
    if (numString.isNotEmpty()) numbers += " $numString"

    return numbers.trim().split(" ").toTypedArray()
}

fun parseOperators(input: String):Array<String> {
    var operators = ""

    for (i in input.indices) {
        val char = input[i]
        if (i == 0 && char in "-+")  continue
        if (char.isDigit()) continue
        if (char in "-+") {
            operators += " $char"
        }
    }

    return if (operators == "") emptyArray() else operators.trim().split(" ").toTypedArray()
}
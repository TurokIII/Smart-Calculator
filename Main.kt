package calculator
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.*

fun main() {
    getInput()
}

fun getInput() {
    val scan = Scanner(System.`in`)
    val variables = mutableMapOf<String, String>()

    while (true) {
        val input = scan.nextLine().trim()

        when {
            input == "/exit" -> break
            input == "/help" -> println("The program calculates the sum of numbers")
            input == "" -> {}
            input[0] == '/' -> println("Unknown command")
            isVariableAssignment(input) -> saveVariable(input, variables)
            else -> toPostFix(input, variables)
                //calculateResult(input, variables)
        }
        //println("MAP HAS:   $variables")
    }
    println("Bye!")
}

fun isVariableAssignment(input: String): Boolean {
    return input.contains("=")
}

fun isValidAssignment(input: String): Boolean {
    val varRegex = Regex("""[a-zA-Z]+\s*=\s*(-?\d+|[a-zA-Z]+)""")
    return input.matches(varRegex)
}

fun getVariableName(input: String): String {
    val inputClean = input.replace(" ", "")
    return inputClean.substring(0, inputClean.indexOf("="))
}

fun getVariableValue(input: String): String {
    val inputClean = input.replace(" ", "")
    return inputClean.substring(inputClean.indexOf("=") + 1)
}

fun saveVariable(input: String, variables: MutableMap<String, String>): MutableMap<String, String> {
    val assignedCorrectly = isValidAssignment(input)

    if (assignedCorrectly) {
        val variableValue = getVariableValue(input)
        val variableName = getVariableName(input)

        if (isValidValue(variableValue, variables)) {
            if (variables.containsKey(variableValue)) {
                val referencedValue = variables.getOrDefault(variableValue, "")
                variables[variableName] = referencedValue
            } else {
                variables[variableName] = variableValue
            }
        } else {
            println("Invalid assignment")
        }
    } else {
        println("Invalid assignment")
    }

    return variables
}

fun toPostFix(input: String, variables: MutableMap<String, String>): String {
    val formulaList = mutableListOf<String>()
    val stack = Stack<String>()
    val pieces = input.split(" ")

    for (e in pieces) {
        when {
            isNumber(e) -> formulaList.add(e)
            isVariable(e) -> formulaList.add(e)
            isOperator(e) -> {
                if (stack.isEmpty() || stack.peek() == "(") {
                    stack.push(e)
                    continue
                }
                if (operatorPrecedence(e) > operatorPrecedence(stack.peek())) {
                    stack.push(e)
                } else {
                    while (operatorPrecedence(e) < operatorPrecedence(stack.peek()))
                }
            }
        }
    }


    return ""
}

fun operatorPrecedence(operator: String): Int {
    return when (operator) {
        "(" -> 3
        ")" -> 3
        "/" -> 2
        "*" -> 2
        "+" -> 1
        "-" -> 1
        else -> 0
    }
}

fun calculateResult(input: String, variables: MutableMap<String, String>) {
    val cleanInput = reduceOperators(input)

    if (variables.containsKey(input)) {
        println(variables[input])
        return
    }

    try {
        println(evaluate(cleanInput, variables).toString())
    } catch (e: NumberFormatException) {
        println("Invalid Expression")  // Could write more useful message, but needs to be this to pass tests
    } catch (e: Exception){
        println(e.message)
    }
}

fun evaluate(input: String, variables: MutableMap<String, String>): Int {
    var result = 0
    var operator = "+"
    val pieces = input.split(" ")

    for (element in pieces) {
        when {
            isNumber(element) -> {
                result = executeOperation(result, element.toInt(), operator)
                continue
            }
            isOperator(element) -> {
                operator = element
            }
            isVariable(element) -> {
                if (variables.containsKey(element)) {
                    val varValue = variables[element]!!.toInt()
                    result = executeOperation(result, varValue, operator)
                } else {
                    throw Exception("Unknown variable")
                }
            }
        }
    }

    return result
}

fun executeOperation(result: Int, number: Int, operation: String): Int {
    return if (operation == "+") result + number else result - number
}

fun reduceOperators(input: String): String {
    var result = input
    while (result.contains("++")) { result = result.replace("++", "+") }
    while (result.contains("--")) { result = result.replace("--", "+") }
    while (result.contains("-+")) { result = result.replace("-+", "-") }
    while (result.contains("+-")) { result = result.replace("+-", "-") }
    while (result.contains("++")) { result = result.replace("++", "+") }

    return result
}

fun isNumber(input: String): Boolean {
    val numRegex = Regex("""-?\d+""")
    return input.matches(numRegex)
}

fun isOperator(input: String): Boolean {
    return input in "-+/*"
}

fun isVariable(input: String): Boolean {
    val varRegex = Regex("""[a-zA-z]+""")
    return input.matches(varRegex)
}

fun isValidValue(value: String, variables: MutableMap<String, String>): Boolean {

//    println("ISNUMBER:  ${isNumber(value)}")
//    println("INMAP:  ${variables.containsKey(value)}")
    return isNumber(value) || variables.containsKey(value)
}

fun parseNumbers(input: String): IntArray {
    val inputs = input.split(" ")
    var numbers = intArrayOf()

    for (i in inputs.indices step 2) {
        val number = inputs[i].toInt()
        numbers += number
    }
    return numbers
}

fun parseOperators(input: String): Array<String> {
    val inputs = input.split(" ")
    var operators = emptyArray<String>()

    for (i in 1 until inputs.size step 2) {
        val operator = inputs[i]
        if (operator in "-+") operators += operator else throw Exception()
    }
    return operators
}
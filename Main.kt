package calculator
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.*
import java.math.BigInteger

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
            else -> {
                try {
                    toPostfix(input, variables)
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
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

fun parseVariableName(input: String): String {
    val inputClean = input.replace(" ", "")
    return inputClean.substring(0, inputClean.indexOf("="))
}

fun parseVariableValue(input: String): String {
    val inputClean = input.replace(" ", "")
    return inputClean.substring(inputClean.indexOf("=") + 1)
}

fun saveVariable(input: String, variables: MutableMap<String, String>): MutableMap<String, String> {
    val assignedCorrectly = isValidAssignment(input)

    if (assignedCorrectly) {
        val variableValue = parseVariableValue(input)
        val variableName = parseVariableName(input)

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

fun toPostfix(input: String, variables: MutableMap<String, String>) {
    val formulaList = mutableListOf<String>()
    val stack = Stack<String>()
    val cleanInput = sanitize(input).replace("(", "( ").replace(")", " )")
    val pieces = cleanInput.split(" ")

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
                    while (operatorPrecedence(e) <= operatorPrecedence(stack.peek()) && stack.peek() != "(") {
                        formulaList.add(stack.pop())
                        if (stack.isEmpty()) {
                            break
                        }
                    }
                    stack.push(e)
                }
            }
            e == "(" -> stack.push(e)
            e == ")" -> {
                while (stack.peek() != "(") {
                    formulaList.add(stack.pop())
                }
                stack.pop() // get rid of the left parens remaining on top of stack
            }
        }
    }

    while (!stack.isEmpty()) {
        formulaList.add(stack.pop())
    }

    evaluatePostfix(formulaList, variables)
}

fun evaluatePostfix(elements: MutableList<String>, variables: MutableMap<String, String>) {
    val stack = Stack<String>()

    for (e in elements) {
        when {
            isNumber(e) -> stack.push(e)
            isVariable(e) -> stack.push(getVariableValue(e, variables))
            isOperator(e) -> {
                val num2 = stack.pop()
                val num1 = stack.pop()
                val result = evaluateOperation(num1,num2, e)
                stack.push(result)
            }
        }
    }
    println(stack.pop())
}

fun evaluateOperation(operand1: String, operand2: String, operator: String): String {
    val result: BigInteger
    val num1 = operand1.toBigInteger()
    val num2 = operand2.toBigInteger()


    result = when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> num1 / num2
        else -> throw Exception("Unknown operator")
    }

    return result.toString()
}

fun getVariableValue(variable: String, variables: MutableMap<String, String>): String {
    val result: String

    if (variables.containsKey(variable)) {
        result = variables.getOrDefault(variable, "0")
    } else {
        throw Exception("Unknown variable")
    }

    return result
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

fun sanitize(input: String): String {
    var leftCount = 0
    var rightCount = 0

    for (c in input) {
        if (c == '(') leftCount++
        if (c == ')') rightCount++
    }

    if (leftCount != rightCount) throw Exception("Invalid Expression")
    if (input.contains("**") || input.contains("//")) throw Exception("Invalid Expression")

    var result = reduceOperators(input)
    val plusRegex = Regex("""\+""")
    val subRegex = Regex("""-""")
    val mulRegex = Regex("""\*""")
    val divRegex = Regex("""/""")
    result = result.replace(plusRegex, " + ")
    result = result.replace(subRegex, " - ")
    result = result.replace(mulRegex, " * ")
    result = result.replace(divRegex, " / ")
    result = result.replace("  ", " ")

    return result
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
    return isNumber(value) || variables.containsKey(value)
}
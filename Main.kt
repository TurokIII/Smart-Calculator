package calculator
import java.util.Scanner

fun main() {
    val scan = Scanner(System.`in`)
    while (true) {
        val input = scan.nextLine()
        if (input == "/exit") break
        if (input == "") continue
        val nums = input.split(" ").toTypedArray()

        println(addNumbers(nums))
    }
    println("Bye!")
}

fun addNumbers(nums: Array<String>): Int {
    var sum = 0

    for (s in nums) {
        if (s != " ") sum += s.toInt()
    }

    return sum
}
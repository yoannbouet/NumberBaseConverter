package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.system.exitProcess

// Do not delete this line

class Number {
    var sourceBase: String = ""
    var targetBase: BigInteger = BigInteger.ZERO
}

const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun main() {
    print("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
    val input = readln()

    if (input == "/exit") exitProcess(0)

    val n = Number()
    val list = input.split(" ")
    n.sourceBase = list.first()
    n.targetBase = list.last().toBigInteger()

    prompt(n.sourceBase, n.targetBase)
}

fun prompt(nSrc: String, nTgt: BigInteger) {
    println("Enter number in base $nSrc to convert to base $nTgt (To go back type /back)")
    val n = readln()

    if (n == "/exit") exitProcess(0) else if (n == "/back") main()

    val nIntegerStr = if ('.' in n) n.substringBefore(".") else n
    val nFractionStr = if ('.' in n) n.substringAfter(".") else ""

    if ('.' in n) println("Conversion result: ${integerConverter(nIntegerStr, nSrc, nTgt)}." +
            fractionConverter(nFractionStr, nSrc, nTgt.toBigDecimal()).substringAfter("."))
    else println("Conversion result: ${integerConverter(nIntegerStr, nSrc, nTgt)}")

    prompt(nSrc, nTgt)
}

fun integerConverter(x: String, src: String, tgt: BigInteger): String {
    // Any to Decimal
    var n = x.uppercase().map {if (it in ALPHABET) it.code - 55 else it }.toList().asReversed().
            mapIndexed {
                    index, it -> it.toString().toBigDecimal() * src.toBigDecimal().pow(index)
            }.sumOf { it.toBigInteger() }

    // Decimal to Any
    var result = if (n - tgt * (n / tgt) >= BigInteger.TEN) {
        ((n - tgt * (n / tgt)).toInt() + 55).toChar().toString()
    } else (n - tgt * (n / tgt)).toString()

    while (n / tgt != BigInteger.ZERO) {
        n /= tgt
        result += if (n - tgt * (n / tgt) >= BigInteger.TEN) {
            ((n - tgt * (n / tgt)).toInt() + 55).toChar()
        } else n - tgt * (n / tgt)
    }

    return result.reversed()
}

fun fractionConverter(x: String, src: String, tgt: BigDecimal): String {
    // Any to Decimal
    var n = x.uppercase().map {if (it in ALPHABET) it.code - 55 else it }.toList().
            mapIndexed {
                    index: Int, it -> it.toString().toBigDecimal() *
                    (src.toBigDecimal().pow(-index - 1, MathContext.DECIMAL64))
            }.sumOf { it }

    // Decimal to Any
    n *= tgt
    var result = if (n >= BigDecimal.TEN) {
        (n.toString().substring(0, 2).toInt() + 55).toChar().toString()
    } else n.toString().first().toString()

    repeat(4) {
        n = if (n >= BigDecimal.TEN) {
            ('0' + n.toString().substring(2, n.toString().lastIndex)).toBigDecimal() * tgt
        } else ('0' + n.toString().substringAfter(n.toString().first())).toBigDecimal() * tgt
        result += if (n >= BigDecimal.TEN) {
            ((n).toString().substring(0, 2).toInt() + 55).toChar()
        } else n.toString().first()
    }

    return result
}
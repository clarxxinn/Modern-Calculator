package com.example.modernappcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.math.BigInteger
import java.text.DecimalFormat

class CalculatorState {
    var expression: String by mutableStateOf("")
        private set
    var result: String by mutableStateOf("")
        private set

    private var justEvaluated: Boolean = false

    private val operators = charArrayOf('+', '-', '*', '/')

    fun onDigit(digit: Char) {
        if (justEvaluated) {
            expression = digit.toString()
            result = ""
            justEvaluated = false
        } else {
            val lastNumber = currentNumberToken()
            if (lastNumber == "0") {
                expression = expression.dropLast(1) + digit
            } else {
                expression += digit
            }
        }
    }

    fun onDecimal() {
        if (justEvaluated) {
            expression = "0."
            result = ""
            justEvaluated = false
            return
        }
        val current = currentNumberToken()
        if (current.isEmpty()) {
            expression += "0."
        } else if (!current.contains(".")) {
            expression += "."
        }
    }

    fun onOperator(op: Char) {
        if (expression.isEmpty()) {
            if (op == '-') {
                expression = "-"
                justEvaluated = false
            }
            return
        }
        if (justEvaluated) {
            expression = (if (result.isNotEmpty()) result.replace(",", "") else expression) + op
            result = ""
            justEvaluated = false
            return
        }
        expression = if (expression.last() in operators) {
            expression.dropLast(1) + op
        } else {
            expression + op
        }
    }

    fun onToggleSign() {
        val start = lastNumberStartIndex()
        if (start == -1) return
        val end = expression.length
        val token = expression.substring(start, end)
        if (token.isEmpty()) return

        val negated = if (start > 0 && expression[start - 1] == '-' &&
            (start - 1 == 0 || expression[start - 2] in operators)
        ) {
            expression = expression.substring(0, start - 1) + expression.substring(start)
            return
        } else {
            "-$token"
        }
        expression = expression.substring(0, start) + negated
    }

    fun onBackspace() {
        if (justEvaluated) {
            onClear()
            return
        }
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
    }

    fun onClear() {
        expression = ""
        result = ""
        justEvaluated = false
    }

    fun onEquals() {
        if (expression.isEmpty()) return
        val cleanExpr = trimTrailingOperator(expression)
        val evaluated = evaluate(cleanExpr)
        if (evaluated != null) {
            expression = cleanExpr
            result = evaluated
            justEvaluated = true
        }
    }

    private fun currentNumberToken(): String {
        val start = lastNumberStartIndex()
        return if (start == -1) "" else expression.substring(start)
    }

    private fun lastNumberStartIndex(): Int {
        if (expression.isEmpty()) return -1
        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) i--
        return i + 1
    }

    private fun trimTrailingOperator(expr: String): String {
        var e = expr
        while (e.isNotEmpty() && e.last() in operators) {
            e = e.dropLast(1)
        }
        return e
    }

    private fun evaluate(expr: String): String? {
        return try {
            val value = ExpressionParser(expr).parse()
            formatNumberString(trimTrailingZeros(value))
        } catch (e: Exception) {
            null
        }
    }

    private fun trimTrailingZeros(value: Double): String {
        if (value.isNaN() || value.isInfinite()) throw ArithmeticException("Invalid result")
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            var s = String.format("%.10f", value)
            s = s.trimEnd('0').trimEnd('.')
            s
        }
    }

    companion object {
        fun formatDisplayExpression(expr: String): String {
            if (expr.isEmpty()) return ""
            val sb = StringBuilder()
            val numBuffer = StringBuilder()

            fun flush() {
                if (numBuffer.isNotEmpty()) {
                    sb.append(formatNumberString(numBuffer.toString()))
                    numBuffer.clear()
                }
            }

            var i = 0
            while (i < expr.length) {
                val c = expr[i]
                if (c.isDigit() || c == '.') {
                    numBuffer.append(c)
                } else if (c == '-' && (i == 0 || expr[i - 1] in charArrayOf('+', '-', '*', '/'))) {
                    flush()
                    numBuffer.append(c)
                } else {
                    flush()
                    sb.append(" ").append(displayOperator(c)).append(" ")
                }
                i++
            }
            flush()
            return sb.toString().trim()
        }

        private fun displayOperator(op: Char): String = when (op) {
            '*' -> "×"
            '/' -> "÷"
            else -> op.toString()
        }

        fun formatNumberString(numStr: String): String {
            if (numStr.isEmpty() || numStr == "-") return numStr
            val negative = numStr.startsWith("-")
            val unsigned = if (negative) numStr.substring(1) else numStr
            val parts = unsigned.split(".")
            val rawInt = parts[0].ifEmpty { "0" }
            val formattedInt = try {
                DecimalFormat("#,###").format(BigInteger(rawInt))
            } catch (e: Exception) {
                rawInt
            }
            val sign = if (negative) "-" else ""
            return if (parts.size > 1) "$sign$formattedInt.${parts[1]}" else "$sign$formattedInt"
        }
    }
}

private class ExpressionParser(private val text: String) {
    private var pos = 0

    fun parse(): Double {
        if (text.isEmpty()) throw IllegalArgumentException("Empty expression")
        val value = parseExpression()
        if (pos != text.length) throw IllegalArgumentException("Unexpected trailing characters")
        return value
    }

    private fun parseExpression(): Double {
        var value = parseTerm()
        while (pos < text.length && (text[pos] == '+' || text[pos] == '-')) {
            val op = text[pos]
            pos++
            val rhs = parseTerm()
            value = if (op == '+') value + rhs else value - rhs
        }
        return value
    }

    private fun parseTerm(): Double {
        var value = parseFactor()
        while (pos < text.length && (text[pos] == '*' || text[pos] == '/')) {
            val op = text[pos]
            pos++
            val rhs = parseFactor()
            if (op == '/') {
                if (rhs == 0.0) throw ArithmeticException("Division by zero")
                value /= rhs
            } else {
                value *= rhs
            }
        }
        return value
    }

    private fun parseFactor(): Double {
        if (pos < text.length && text[pos] == '-') {
            pos++
            return -parseFactor()
        }
        if (pos < text.length && text[pos] == '+') {
            pos++
            return parseFactor()
        }
        val start = pos
        while (pos < text.length && (text[pos].isDigit() || text[pos] == '.')) pos++
        if (start == pos) throw IllegalArgumentException("Invalid number")
        return text.substring(start, pos).toDouble()
    }
}

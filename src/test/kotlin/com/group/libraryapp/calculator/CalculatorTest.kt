package com.group.libraryapp.calculator

import com.group.libraryapp.calulator.Calculator

fun main () {
    val calculatorTest = CalculatorTest()
    calculatorTest.add()
    calculatorTest.minus()
    calculatorTest.multiply()
    calculatorTest.divide()
    calculatorTest.divideException()
}

class CalculatorTest {

    fun add() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.add(3)

        // then
        if (calculator.number != 8) {
            throw IllegalArgumentException()
        }
    }

    fun minus() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.minus(3)

        // then
        if (calculator.number != 2) {
            throw IllegalArgumentException()
        }
    }

    fun multiply() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.multiply(3)

        // then
        if (calculator.number != 15) {
            throw IllegalArgumentException()
        }
    }

    fun divide() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.divide(2)

        // then
        if (calculator.number != 2) { // 몫은 2, 나머지는 0.5 중 몫반 반환하기 떄문에
            throw IllegalArgumentException()
        }
    }

    fun divideException() {
        // given
        val calculator = Calculator(5)

        // when
        try {
            calculator.divide(0)
        } catch (e: IllegalArgumentException) {
            if (e.message != "0으로 나눌 수 없습니다") {
                throw IllegalStateException("메시지가 다릅니다")
            }
            // 테스트 성공
            return
        } catch (e: Exception) {
            throw IllegalStateException()
        }

        throw IllegalStateException("기대하는 에러가 발생하지 않았습니다")
    }
}
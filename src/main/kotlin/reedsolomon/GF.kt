package reedsolomon

import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

data class GF(var data: Int = 0) {
    val inverseTable = Array(q) { idx -> if (idx == 0) 0 else (1 until q).first { (it * idx) % q == 1 } }

    operator fun plus(inc: GF) = GF(data + inc.data)

    operator fun minus(dec: GF) = GF(data - dec.data)

    operator fun times(mul: GF) = GF(data * mul.data)

    operator fun div(div: GF): GF {
        require(div.data != 0) { "Деление на 0 невозможно в соответствии с аксиомами поля!" }
        return GF(data * inverseTable[div.data])
    }

    operator fun unaryMinus() = GF(-data)

    operator fun inc() = GF(data + 1)

    init {
        data = mod(data, q)
    }

    companion object {
        var q: Int = 11
            set(value) {
                require(isPrime(value)) { "q должно быть простым!" }
                field = value
            }

        fun isPrime(q: Int): Boolean {
            if (q < 2) return false
            return (2..sqrt(q.toDouble()).roundToInt())
                .all { q % it != 0 }
        }

        fun mod(n: Int, d: Int): Int {
            var result = n % d
            if (result.sign * d.sign < 0)
                result += d
            return result
        }
    }
}

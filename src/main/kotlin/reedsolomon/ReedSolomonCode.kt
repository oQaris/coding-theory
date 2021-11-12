package reedsolomon

class ReedSolomonCode(val n: Int, t: Int) {
    val k = n - 2 * t
    val h = fillH(n, k)
    val g = fillGbyH(h)
    private val syndromeError = fillSyndrome(h)

    fun encode(message: Array<GF>): Array<GF> {
        if (message.size != k)
            throw IllegalArgumentException("Wrong info length")
        val encoded = Array(n) { GF(0) }
        for (i in 0 until n)
            for (j in 0 until k)
                encoded[i] += message[j] * g[j][i]
        return encoded
    }

    fun decode(encoded: Array<GF>): Array<GF> {
        if (encoded.size != n)
            throw IllegalArgumentException("Wrong info length")

        val decoded = Array(k) { i -> encoded[i] }
        val syndrome = Array(n - k) { GF(0) }

        for (i in 0 until n - k)
            for (j in 0 until n)
                syndrome[i] += encoded[j] * h[i][j]

        val error = syndromeError[syndrome.toList()]
            ?: throw IllegalArgumentException("Failed to decode")

        for (i in decoded.indices)
            decoded[i] -= error[i]
        return decoded
    }
}

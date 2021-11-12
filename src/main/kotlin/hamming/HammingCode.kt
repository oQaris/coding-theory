package hamming

fun encode(bin: String, m: Int = 3): String {
    val n = (2 shl m - 1) - 1 // 2^m - 1
    val k = n - m
    // Новый массив размера: old_size * n / k
    val res = StringBuilder(((bin.length - 1) / k + 1) * n)
    bin.chunked(k).forEach { bytes ->
        res.append(bytes)
        val sum = bytes.map { it.toBin() }.sum()
        // Проверочные биты
        res.append((sum - bytes[3].toBin()) % 2)
        res.append((sum - bytes[0].toBin()) % 2)
        res.append((sum - bytes[2].toBin()) % 2)
    }
    return res.toString()
}

val syndromes = arrayOf(
    "101",
    "111",
    "110",
    "011",
    /*"100",
    "010",
    "001"*/
)

fun decode(bin: String, m: Int = 3): String {
    val n = (2 shl m - 1) - 1
    val k = n - m
    // Новый массив размера: old_size * k / n
    val res = StringBuilder(((bin.length - 1) / n + 1) * k)
    bin.chunked(n).forEach { bytes ->
        val data = StringBuilder(bytes.dropLast(m))
        val syn = StringBuilder(bytes.takeLast(m))
        val sum = data.map { it.toBin() }.sum()
        // Вычисление синдрома
        syn[0] = ((syn[0].toBin() + sum - bytes[3].toBin()) % 2).toBin()
        syn[1] = ((syn[1].toBin() + sum - bytes[0].toBin()) % 2).toBin()
        syn[2] = ((syn[2].toBin() + sum - bytes[2].toBin()) % 2).toBin()
        if (syn.toString() != "000") {
            val errPos = syndromes.indexOf(syn.toString())
            if (errPos != -1)
                data[errPos] = ((data[errPos].toBin() + 1) % 2).toBin()
        }
        res.append(data)
    }
    return res.toString()
}

fun Char.toBin() = this.toString().toInt()

fun Int.toBin() = this.toString()[0]

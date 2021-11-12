package reedsolomon

import com.github.shiguruikai.combinatoricskt.CombinationsWithRepetitionGenerator
import com.github.shiguruikai.combinatoricskt.permutations

// Возвращает матрицу H
fun fillH(n: Int, k: Int) = Array(n - k) { i -> Array(n) { j -> GF(j + 1).pow(i + 1) } }

// Возведение в степень в поле GF(p)
fun GF.pow(n: Int) = (2..n).fold(this) { r, _ -> r * this }

// Возвращает матрицу G
fun fillGbyH(h: Array<Array<GF>>): Array<Array<GF>> {
    val n = h.cols()
    val k = n - h.rows()
    val out = Array(k) { i -> Array(n) { j -> GF(if (i == j || j >= k) 1 else 0) } }
    val ght = Array(n - k) { Array(n - k + 1) { GF(0) } }

    for (i in 0 until k) {
        for (j in 0 until n - k) {
            ght[j][n - k] = -h[j][i]

            for (z in k until n)
                ght[j][z - k] = h[j][z]
        }

        for (j in 0 until n - k) {
            var diag = ght[j][j]
            var z = j + 1
            while (diag.data == 0 && z < n - j) {
                if (ght[z][j].data != 0) {
                    ght[j] = ght[z].also { ght[z] = ght[j] }
                    diag = ght[j][j]
                }
                z++
            }

            if (diag.data == 0)
                throw IllegalArgumentException("Could not find non-zero diagonal element")

            for (x in 0 until n - k + 1)
                ght[j][x] /= diag

            nulling(ght, j, n - k, false)
        }

        for (j in 0 until n - k - 1)
            nulling(ght, j, n - k, true)

        for (j in k until n)
            out[i][j] = ght[j - k][n - k]
    }
    return out
}

// Зануление элементов над/под диагональю
private fun nulling(mtx: Array<Array<GF>>, start: Int, lim: Int, isAbove: Boolean) {
    for (u in start + 1 until lim) {
        val outDiag = if (isAbove) mtx[start][u] else mtx[u][start]
        if (outDiag.data != 0) {
            if (isAbove) mtx[start][u] else mtx[u][start] = GF(0)
            for (x in (if (isAbove) u else start) + 1 until lim + 1)
                mtx[if (isAbove) start else u][x] -= mtx[if (isAbove) u else start][x] * outDiag
        }
    }
}

// Заполнение таблицы синдромов
@OptIn(ExperimentalStdlibApi::class)
fun fillSyndrome(h: Array<Array<GF>>) = buildMap<List<GF>, Array<GF>> {
    val n = h.cols()
    val k = n - h.rows()
    val t = (n - k) / 2
    CombinationsWithRepetitionGenerator.indices(n + 1, t).forEach { snd3 ->
        snd3.plus(IntArray(n - t) { 0 }).toList().permutations(GF.q - 1).distinct().forEach { err ->
            val syndrome = Array(n - k) { GF(0) }
            for (i in 0 until n - k)
                for (j in 0 until n)
                    syndrome[i] += GF(err[j]) * h[i][j]
            put(syndrome.toList(), err.map { GF(it) }.toTypedArray())
        }
    }
}

// Транспонирование матрицы
fun Array<Array<GF>>.transpose() = Array(this[0].size) { i -> Array(this.size) { j -> this[j][i] } }

// Перемножение двух матриц
fun Array<Array<GF>>.mul(mul: Array<Array<GF>>): Array<Array<GF>> {
    require(this.cols() == mul.rows()) { "Некорректные размеры перемножаемых матриц! ${this.cols()} на ${mul.rows()}" }
    val mulT = mul.transpose()
    return Array(this.rows()) { rowA ->
        Array(mul.cols()) { colB ->
            this[rowA].zip(mulT[colB]).fold(GF(0)) { acc, p ->
                acc + p.first * p.second
            }
        }
    }
}

fun Array<Array<GF>>.toStr() = this.joinToString("\n") { row -> row.joinToString(" ") { it.data.toString() } }

fun Array<Array<GF>>.rows() = this.size
fun Array<Array<GF>>.cols() = this[0].size


// За следующие тестовые методы спасибо Дмитрию Патоке

fun messageToFourWords(message: String): List<Array<GF>> {
    val bytes = message.encodeToByteArray().map { it.toUByte() }
    val byteThirds = mutableListOf<Int>()
    for (byte in bytes) {
        val byteInt = byte.toInt()
        // Делим символ на 3 числа
        byteThirds.add(byteInt shr 5)
        byteThirds.add((byteInt and 0b00011100) shr 2)
        byteThirds.add(byteInt and 0b00000011)
    }

    var extraZeros = 0
    while (byteThirds.size % 4 != 0) {
        byteThirds.add(0)
        extraZeros++
    }

    val gf11Numbers = byteThirds.map { GF(it) }
    val fourWords = mutableListOf<Array<GF>>()
    for (i in gf11Numbers.indices step 4)
        fourWords.add(
            arrayOf(
                gf11Numbers[i],
                gf11Numbers[i + 1],
                gf11Numbers[i + 2],
                gf11Numbers[i + 3]
            )
        )
    fourWords.add(arrayOf(GF(extraZeros), GF(), GF(), GF()))
    return fourWords
}

@OptIn(ExperimentalStdlibApi::class)
fun fourWordsToMessage(fourWords: List<Array<GF>>): String {
    val extraZeros = fourWords.last()[0].data
    val byteThirds = buildList {
        fourWords.dropLast(1)
            .forEach { word ->
                require(word.size == 4)
                addAll(word)
            }
    }.dropLast(extraZeros)
        .map { it.data }

    val bytes = mutableListOf<Byte>()
    for (i in byteThirds.indices step 3)
        bytes.add(((byteThirds[i] shl 5) + (byteThirds[i + 1] shl 2) + byteThirds[i + 2]).toByte())

    return String(bytes.toByteArray(), Charsets.UTF_8)
}

fun printGFMatrix(matrix: Array<Array<GF>>) {
    matrix.forEach { str ->
        str.forEach { num ->
            print(num.data)
            print("\t")
        }
        println()
    }
    println()
}

fun printWordList(wordList: List<Array<GF>>) {
    wordList.forEach { word -> println(word.map { num -> num.data }.toList()) }
    println()
}

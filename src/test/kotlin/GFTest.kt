import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reedsolomon.GF
import reedsolomon.mul
import reedsolomon.toStr
import kotlin.test.assertEquals

internal class GFTest {

    @Test
    fun isPrimeTest() {
        val primes = listOf(2, 3, 5, 7, 11, 37, 101)
        val notPrimes = listOf(1, 0, -5, 4, 10, 100)

        assertTrue(primes.all { GF.isPrime(it) })
        assertTrue(notPrimes.all { !GF.isPrime(it) })
    }

    @Test
    fun modTest() {
        val q = 5
        val nums = listOf(0, 1, 4, 5, -1, -5, 11, -14, 97, -7)
        val mods = listOf(0, 1, 4, 0, 4, 0, 1, 1, 2, 3)

        assertEquals(mods, nums.map { GF.mod(it, q) })
    }

    @Test
    fun inverseTableTest() {
        assertEquals(
            listOf(0, 1, 6, 4, 3, 9, 2, 8, 7, 5, 10),
            GF(11).inverseTable.toList()
        )
    }

    @Test
    fun mulTest() {
        val a = arrayOf(
            arrayOf(GF(1), GF(2), GF(4)),
            arrayOf(GF(2), GF(0), GF(3))
        )
        val b = arrayOf(
            arrayOf(GF(2), GF(5)),
            arrayOf(GF(1), GF(3)),
            arrayOf(GF(1), GF(1))
        )

        assertEquals(
            arrayOf(
                arrayOf(GF(8), GF(4)),
                arrayOf(GF(7), GF(2))
            ).toStr(),
            a.mul(b).toStr()
        )
    }

    @Test
    fun prodTest() {
        //prod(4, 3).forEach { println(it.joinToString(" ")) }
        (0..3).toList().permutationsWithRepetition(4).forEach { println(it.joinToString(" ")) }
    }
}

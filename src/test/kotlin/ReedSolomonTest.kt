import org.ejml.simple.SimpleMatrix
import org.junit.jupiter.api.Test

internal class ReedSolomonTest {

    /*@Test
    fun genMatrixHTest() {
        val p = 11
        val h = reedsolomon.genMatrixH(p, 3)

        assertEquals(
            arrayOf(
                arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                arrayOf(1, 4, 9, 5, 3, 3, 5, 9, 4, 1),
                arrayOf(1, 8, 5, 9, 4, 7, 2, 6, 3, 10),
                arrayOf(1, 5, 4, 3, 9, 9, 3, 4, 5, 1),
                arrayOf(1, 10, 1, 1, 1, 10, 10, 10, 1, 10),
                arrayOf(1, 9, 3, 4, 5, 5, 4, 3, 9, 1)
            ), h
        )
    }*/

    @Test
    fun nullSpaceTest() {
        val data =
            arrayOf(
                doubleArrayOf(1.0, 2.0, 0.0),
                doubleArrayOf(2.0, 4.0, 0.0),
                doubleArrayOf(3.0, 6.0, 1.0)
            )

        val m = SimpleMatrix(data)
        val kernel = m.svd().nullSpace()

        for (i in 0 until 3)
            println(kernel.get(i).toString())

        println(m.mult(kernel))
    }
}

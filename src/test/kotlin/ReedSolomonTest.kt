import org.junit.jupiter.api.Test

internal class ReedSolomonTest {

    @Test
    fun genMatrixHTest() {
        val p = 11
        println(genMatrixH(p, 3).normalize(p).joinToString("\n") { it.joinToString(" ") })
    }
}

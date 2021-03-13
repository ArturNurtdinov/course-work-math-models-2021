import model.Right
import tests.TestProvider
import tests.implementations.SecondTest
import kotlin.math.absoluteValue
const val EPSILON = 1E-10

fun main() {
    val nx = 64
    val ny = 64
    val l = nx + 1
    val testProvider: TestProvider = SecondTest
    val hi = (testProvider.b() - testProvider.a()) / nx
    val hj = (testProvider.d() - testProvider.c()) / ny
    val ans = mutableListOf<Right>()
    for (i in 0..nx) {
        for (j in 0..ny) {
            val m = j * l + i + 1
            ans.add(Right(m, testProvider.u(testProvider.x(i.toDouble(), hi), testProvider.y(j.toDouble(), hj))))
        }
    }
    val result = gradientMethod(testProvider, l, nx, ny)
    println(vectorBinary(result.first, ans.sortedBy { it.m }.map { it.value }, Double::minus).map { it.absoluteValue }.maxOf { it })
    println("result k = ${result.second}, Nx = $nx, Ny = $ny, epsilon = $EPSILON")
}



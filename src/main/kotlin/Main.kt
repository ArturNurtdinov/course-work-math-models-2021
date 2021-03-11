import model.Right
import tests.TestProvider
import tests.implementations.FirstTest
import tests.implementations.SecondTest
import kotlin.math.absoluteValue

const val Nx = 16
const val Ny = 16
const val EPSILON = 1E-15

fun main() {
    val l = Nx + 1
    val testProvider: TestProvider = FirstTest
    val hi = (testProvider.b() - testProvider.a()) / Nx
    val hj = (testProvider.d() - testProvider.c()) / Ny
    val ans = mutableListOf<Right>()
    for (i in 0..Nx) {
        for (j in 0..Ny) {
            val m = j * l + i + 1
            ans.add(Right(m, testProvider.u(testProvider.x(i.toDouble(), hi), testProvider.y(j.toDouble(), hj))))
        }
    }
    val result = gradientMethod(testProvider, l, Nx, Ny)
    println(vectorBinary(result.first, ans.sortedBy { it.m }.map { it.value }, Double::minus).map { it.absoluteValue }.maxOf { it })
    println("result k = ${result.second}, Nx = $Nx, Ny = $Ny, epsilon = $EPSILON")
}



import model.Right
import tests.TestProvider
import tests.implementations.FirstTest
import tests.implementations.SecondTest
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

const val EPSILON = 1E-5

fun main() {
    val nx = 128
    val ny = 128
    val l = nx + 1
    val testProvider: TestProvider = SecondTest
    println("Test = ${testProvider::class.java.simpleName} for Nx = $nx, Ny = $ny, epsilon = $EPSILON")
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
    println(
        "max absolute error = ${
            vectorBinary(result.first, ans.sortedBy { it.m }.map { it.value }, Double::minus).map { it.absoluteValue }
                .maxOf { it }
        }"
    )
    println("result k = ${result.second}")

    val requiredX = 2.1
    val requiredY = 1.1
    println("our solution = ${solutionAtPoint(requiredX, requiredY, result.first, nx, ny, l, testProvider)}")
    println("actual value = ${testProvider.u(requiredX, requiredY)}")
    println("at point: x = $requiredX y = $requiredY")
}

fun solutionAtPoint(
    requiredX: Double,
    requiredY: Double,
    u: List<Double>,
    nx: Int,
    ny: Int,
    l: Int,
    testProvider: TestProvider
): Double {
    var res: Double = u[0]
    val hx: Double = (testProvider.b() - testProvider.a()) / nx
    val hy: Double = (testProvider.d() - testProvider.c()) / ny
    var minLength = Double.MAX_VALUE
    for (i in 0..nx) {
        for (j in 0..nx) {
            val m = j * l + i
            val length = sqrt(
                (requiredX - testProvider.x(i.toDouble(), hx)).pow(2) + (requiredY - testProvider.y(
                    j.toDouble(),
                    hy
                )).pow(2)
            )
            if (minLength > length) {
                minLength = length
                res = u[m]
            }
        }
    }
    return res
}



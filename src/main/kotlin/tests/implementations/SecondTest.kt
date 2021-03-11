package tests.implementations

import tests.TestProvider
import kotlin.math.pow

object SecondTest : TestProvider {
    val a = 2.0
    val b = 6.0
    val c = 1.0
    val d = 5.0
    val xi = 5.0

    override fun xi(): Double = xi
    override fun a(): Double = a

    override fun b(): Double = b

    override fun c(): Double = c

    override fun d(): Double = d

    override fun x(i: Double, hx: Double): Double = a + i * hx
    override fun y(j: Double, hy: Double): Double = c + j * hy

    override fun k1(x: Double, y: Double): Double = x.pow(2) + 2

    override fun k2(x: Double, y: Double): Double = y.pow(3) + x + 3

    override fun f(x: Double, y: Double): Double =
        -6 * x.pow(2) * y.pow(2) - 4 * y.pow(2) - 8 * x.pow(2) * y.pow(3) - 2 * x.pow(3) - 6 * x.pow(2)

    override fun g1(y: Double): Double = 4 * y.pow(2)

    override fun g2(y: Double): Double = 36 * y.pow(2)

    override fun g3(x: Double): Double = x.pow(2)

    override fun g4(x: Double): Double = 125 * x.pow(2) + (128 + x) * 10 * x.pow(2)

    override fun u(x: Double, y: Double): Double = x.pow(2) * y.pow(2)
}
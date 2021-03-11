package tests.implementations

import tests.TestProvider

object FirstTest : TestProvider {

    const val a = 2.0
    const val b = 6.0
    const val c = 1.0
    const val d = 5.0
    const val xi = 5.0

    override fun a() = a

    override fun b() = b

    override fun c() = c

    override fun d() = d

    override fun x(i: Double, hx: Double): Double = a + i * hx
    override fun y(i: Double, hy: Double): Double = c + i * hy

    fun g1(y: Double): Double = 20.0 * y
    fun g2(y: Double): Double = 60.0 * y
    fun g3(x: Double): Double = 10.0 * x
    fun g4(x: Double): Double = 310.0 * x

    override fun u(x: Double, y: Double): Double = 10.0 * x * y
    fun k1(x: Double, y: Double): Double = 5 * x + 2.0
    fun k2(x: Double, y: Double): Double = y + 1.0

    fun f(x: Double, y: Double): Double = -50.0 * y - 10.0 * x
}
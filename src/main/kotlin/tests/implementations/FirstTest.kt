package tests.implementations

import Nx
import Ny
import model.Coef
import model.Right
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

    override fun getTestData(Hi: Double, Hj: Double, l: Int): Pair<List<Coef>, List<Right>> {
        val res: Pair<MutableList<Coef>, MutableList<Right>> = Pair(mutableListOf(), mutableListOf())
        val coefs = res.first
        val right = res.second
        for (i in 1 until Nx) {
            for (j in 1 until Ny) {
                val m = j * l + i + 1
                coefs.add(Coef(-Hi / Hj * k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)), m, m - l))
                coefs.add(Coef(-Hj / Hi * k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m - 1))
                coefs.add(
                    Coef(
                        Hj / Hi * (k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)) + k1(
                            x(i - 1.0 / 2, Hi),
                            y(j.toDouble(), Hj)
                        ))
                                + Hi / Hj * (k2(x(i.toDouble(), Hi), y(j + 1.0 / 2, Hj)) + k2(
                            x(i.toDouble(), Hi),
                            y(j - 1.0 / 2, Hj)
                        )), m, m
                    )
                )
                coefs.add(Coef(-Hj / Hi * k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m + 1))
                coefs.add(Coef(-Hi / Hj * k2(x(i.toDouble(), Hi), y(j + 1.0 / 2, Hj)), m, m + l))
                right.add(Right(m, Hi * Hj * f(x(i.toDouble(), Hi), y(j.toDouble(), Hj))))
            }
        }
        for (i in 1 until Nx) {
            val j = Ny
            val m = j * l + i + 1
            println(j - 1.0 / 2)
            coefs.add(Coef(-Hi / Hj * k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)), m, m - l))
            coefs.add(Coef(-Hj / 2 / Hi * k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m - 1))
            coefs.add(
                Coef(
                    Hj / 2 / Hi * (k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)) + k1(
                        x(i - 1.0 / 2, Hi),
                        y(j.toDouble(), Hj)
                    )) + Hi * (xi + k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)) / Hj), m, m
                )
            )
            coefs.add(Coef(-Hj / 2 / Hi * k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m + 1))
            right.add(
                Right(
                    m,
                    Hi * Hj / 2 * f(x(i.toDouble(), Hi), y(j.toDouble(), Hj)) + Hi * g4(x(i.toDouble(), Hi))
                )
            )
        }

        for (j in 1..Ny) {
            val i = 0
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, g1(y(j.toDouble(), Hj))))
        }
        for (i in 0..Nx) {
            val j = 0
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, g3(x(i.toDouble(), Hi))))
        }
        for (j in 1..Ny) {
            val i = Nx
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, g2(y(j.toDouble(), Hj))))
        }
        for (i in 1 until Nx) {
            val j = 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m - l }!!
            right.find { it.m == m }!!.value -= g3(x(i.toDouble(), Hi)) * coef.coef
            coefs.remove(coef)
        }
        for (j in 1..Ny) {
            val i = 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m - 1 }!!
            right.find { it.m == m }!!.value -= g1(y(j.toDouble(), Hj)) * coef.coef
            coefs.remove(coef)
        }
        for (j in 1..Ny) {
            val i = Nx - 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m + 1 }!!
            right.find { it.m == m }!!.value -= g2(y(j.toDouble(), Hj)) * coef.coef
            coefs.remove(coef)
        }
        coefs.removeIf { it.ic > it.ir }
        return res
    }


}
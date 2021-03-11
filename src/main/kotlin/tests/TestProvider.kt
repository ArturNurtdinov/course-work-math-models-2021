package tests

import Nx
import Ny
import model.Coef
import model.Right

interface TestProvider {
    fun a(): Double
    fun b(): Double
    fun c(): Double
    fun d(): Double
    fun xi(): Double

    fun k1(x: Double, y: Double): Double
    fun k2(x: Double, y: Double): Double
    fun f(x: Double, y: Double): Double
    fun g1(y: Double): Double
    fun g2(y: Double): Double
    fun g3(x: Double): Double
    fun g4(x: Double): Double

    fun x(i: Double, hx: Double): Double
    fun y(j: Double, hy: Double): Double
    fun u(x: Double, y: Double): Double

    fun getTestData(Hi: Double, Hj: Double, l: Int): Pair<List<Coef>, List<Right>> {
        val res: Pair<MutableList<Coef>, MutableList<Right>> = Pair(mutableListOf(), mutableListOf())
        val coefs = res.first
        val right = res.second
        for (i in 1 until Nx) {
            for (j in 1 until Ny) {
                val m = j * l + i + 1
                coefs.add(Coef(-Hi / Hj * k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)), m, m - l))
                coefs.add(Coef(-Hj / Hi * k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m - 1))
                coefs.add(
                    Coef(Hj / Hi * (k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)) + k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)))
                            + Hi / Hj * (k2(x(i.toDouble(), Hi), y(j + 1.0 / 2, Hj)) + k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj))), m, m
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
            coefs.add(Coef(-Hi / Hj * k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)), m, m - l))
            coefs.add(Coef(-Hj / 2 / Hi * k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m - 1))
            coefs.add(
                Coef(Hj / 2 / Hi * (k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)) + k1(x(i - 1.0 / 2, Hi), y(j.toDouble(), Hj)))
                        + Hi * (xi() + k2(x(i.toDouble(), Hi), y(j - 1.0 / 2, Hj)) / Hj), m, m
                )
            )
            coefs.add(Coef(-Hj / 2 / Hi * k1(x(i + 1.0 / 2, Hi), y(j.toDouble(), Hj)), m, m + 1))
            right.add(
                Right(m, Hi * Hj / 2 * f(x(i.toDouble(), Hi), y(j.toDouble(), Hj)) + Hi * g4(x(i.toDouble(), Hi)))
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
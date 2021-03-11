package tests

import Nx
import Ny
import model.Coef
import model.Right
import tests.implementations.FirstTest

interface TestProvider {
    fun a(): Double
    fun b(): Double
    fun c(): Double
    fun d(): Double

    fun x(i: Double, hx: Double): Double
    fun y(i: Double, hy: Double): Double
    fun u(x: Double, y: Double): Double

    fun getTestData(Hi: Double, Hj: Double, l: Int):Pair<List<Coef>, List<Right>> {
        val res: Pair<MutableList<Coef>, MutableList<Right>> = Pair(mutableListOf(), mutableListOf())
        val coefs = res.first
        val right = res.second
        for (i in 1 until Nx) {
            for (j in 1 until Ny) {
                val m = j * l + i + 1
                coefs.add(Coef(-Hi / Hj * FirstTest.k2(FirstTest.x(i.toDouble(), Hi), FirstTest.y(j - 1.0 / 2, Hj)), m, m - l))
                coefs.add(Coef(-Hj / Hi * FirstTest.k1(FirstTest.x(i - 1.0 / 2, Hi), FirstTest.y(j.toDouble(), Hj)), m, m - 1))
                coefs.add(
                    Coef(
                        Hj / Hi * (FirstTest.k1(
                            FirstTest.x(i + 1.0 / 2, Hi),
                            FirstTest.y(j.toDouble(), Hj)
                        ) + FirstTest.k1(
                            FirstTest.x(i - 1.0 / 2, Hi),
                            FirstTest.y(j.toDouble(), Hj)
                        ))
                                + Hi / Hj * (FirstTest.k2(
                            FirstTest.x(i.toDouble(), Hi),
                            FirstTest.y(j + 1.0 / 2, Hj)
                        ) + FirstTest.k2(
                            FirstTest.x(i.toDouble(), Hi),
                            FirstTest.y(j - 1.0 / 2, Hj)
                        )), m, m
                    )
                )
                coefs.add(Coef(-Hj / Hi * FirstTest.k1(FirstTest.x(i + 1.0 / 2, Hi), FirstTest.y(j.toDouble(), Hj)), m, m + 1))
                coefs.add(Coef(-Hi / Hj * FirstTest.k2(FirstTest.x(i.toDouble(), Hi), FirstTest.y(j + 1.0 / 2, Hj)), m, m + l))
                right.add(Right(m, Hi * Hj * FirstTest.f(FirstTest.x(i.toDouble(), Hi), FirstTest.y(j.toDouble(), Hj))))
            }
        }
        for (i in 1 until Nx) {
            val j = Ny
            val m = j * l + i + 1
            println(j - 1.0 / 2)
            coefs.add(Coef(-Hi / Hj * FirstTest.k2(FirstTest.x(i.toDouble(), Hi), FirstTest.y(j - 1.0 / 2, Hj)), m, m - l))
            coefs.add(Coef(-Hj / 2 / Hi * FirstTest.k1(FirstTest.x(i - 1.0 / 2, Hi), FirstTest.y(j.toDouble(), Hj)), m, m - 1))
            coefs.add(
                Coef(
                    Hj / 2 / Hi * (FirstTest.k1(
                        FirstTest.x(i + 1.0 / 2, Hi),
                        FirstTest.y(j.toDouble(), Hj)
                    ) + FirstTest.k1(
                        FirstTest.x(i - 1.0 / 2, Hi),
                        FirstTest.y(j.toDouble(), Hj)
                    )) + Hi * (FirstTest.xi + FirstTest.k2(
                        FirstTest.x(i.toDouble(), Hi),
                        FirstTest.y(j - 1.0 / 2, Hj)
                    ) / Hj), m, m
                )
            )
            coefs.add(Coef(-Hj / 2 / Hi * FirstTest.k1(FirstTest.x(i + 1.0 / 2, Hi), FirstTest.y(j.toDouble(), Hj)), m, m + 1))
            right.add(
                Right(
                    m,
                    Hi * Hj / 2 * FirstTest.f(
                        FirstTest.x(i.toDouble(), Hi),
                        FirstTest.y(j.toDouble(), Hj)
                    ) + Hi * FirstTest.g4(FirstTest.x(i.toDouble(), Hi))
                )
            )
        }

        for (j in 1..Ny) {
            val i = 0
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, FirstTest.g1(FirstTest.y(j.toDouble(), Hj))))
        }
        for (i in 0..Nx) {
            val j = 0
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, FirstTest.g3(FirstTest.x(i.toDouble(), Hi))))
        }
        for (j in 1..Ny) {
            val i = Nx
            val m = j * l + i + 1
            coefs.add(Coef(1.toDouble(), m, m))
            right.add(Right(m, FirstTest.g2(FirstTest.y(j.toDouble(), Hj))))
        }
        for (i in 1 until Nx) {
            val j = 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m - l }!!
            right.find { it.m == m }!!.value -= FirstTest.g3(FirstTest.x(i.toDouble(), Hi)) * coef.coef
            coefs.remove(coef)
        }
        for (j in 1..Ny) {
            val i = 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m - 1 }!!
            right.find { it.m == m }!!.value -= FirstTest.g1(FirstTest.y(j.toDouble(), Hj)) * coef.coef
            coefs.remove(coef)
        }
        for (j in 1..Ny) {
            val i = Nx - 1
            val m = j * l + i + 1
            val coef = coefs.find { it.ir == m && it.ic == m + 1 }!!
            right.find { it.m == m }!!.value -= FirstTest.g2(FirstTest.y(j.toDouble(), Hj)) * coef.coef
            coefs.remove(coef)
        }
        coefs.removeIf { it.ic > it.ir }
        return res
    }
}
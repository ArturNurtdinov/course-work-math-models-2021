package tests

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

    fun getTestData(Nx: Int, Ny: Int, l: Int): Pair<Map<Pair<Int, Int>, Double>, List<Right>> {
        val res: Pair<MutableMap<Pair<Int, Int>, Double>, MutableList<Right>> = Pair(mutableMapOf(), mutableListOf())
        val hj = (d() - c()) / Ny
        val hi = (b() - a()) / Nx
        val coefs = res.first
        val right = res.second
        for (i in 1 until Nx) {
            for (j in 1 until Ny) {
                val m = j * l + i + 1
                coefs[Pair(m, m - l)] = -hi / hj * k2(x(i.toDouble(), hi), y(j - 1.0 / 2, hj))
                coefs[Pair(m, m - 1)] = -hj / hi * k1(x(i - 1.0 / 2, hi), y(j.toDouble(), hj))
                coefs[Pair(m, m)] = hj / hi * (k1(x(i + 1.0 / 2, hi), y(j.toDouble(), hj)) + k1(
                    x(i - 1.0 / 2, hi),
                    y(j.toDouble(), hj)
                )) + hi / hj * (k2(x(i.toDouble(), hi), y(j + 1.0 / 2, hj)) + k2(
                    x(i.toDouble(), hi),
                    y(j - 1.0 / 2, hj)
                ))
                coefs[Pair(m, m + 1)] = -hj / hi * k1(x(i + 1.0 / 2, hi), y(j.toDouble(), hj))
                coefs[Pair(m, m + l)] = -hi / hj * k2(x(i.toDouble(), hi), y(j + 1.0 / 2, hj))
                right.add(Right(m, hi * hj * f(x(i.toDouble(), hi), y(j.toDouble(), hj))))
            }
        }
        for (i in 1 until Nx) {
            val j = Ny
            val m = j * l + i + 1
            coefs[Pair(m, m - l)] = -hi / hj * k2(x(i.toDouble(), hi), y(j - 1.0 / 2, hj))
            coefs[Pair(m, m - 1)] = -hj / 2 / hi * k1(x(i - 1.0 / 2, hi), y(j.toDouble(), hj))
            coefs[Pair(m, m)] = hj / 2 / hi * (k1(x(i + 1.0 / 2, hi), y(j.toDouble(), hj)) + k1(x(i - 1.0 / 2, hi), y(j.toDouble(), hj))) + hi * (xi() + k2(x(i.toDouble(), hi), y(j - 1.0 / 2, hj)) / hj)
            coefs[Pair(m, m + 1)] = -hj / 2 / hi * k1(x(i + 1.0 / 2, hi), y(j.toDouble(), hj))
            right.add(
                Right(m, hi * hj / 2 * f(x(i.toDouble(), hi), y(j.toDouble(), hj)) + hi * g4(x(i.toDouble(), hi)))
            )
        }

        for (j in 1..Ny) {
            val i = 0
            val m = j * l + i + 1
            coefs[Pair(m, m)] = 1.toDouble()
            right.add(Right(m, g1(y(j.toDouble(), hj))))
        }
        for (i in 0..Nx) {
            val j = 0
            val m = j * l + i + 1
            coefs[Pair(m, m)] = 1.toDouble()
            right.add(Right(m, g3(x(i.toDouble(), hi))))
        }
        for (j in 1..Ny) {
            val i = Nx
            val m = j * l + i + 1
            coefs[Pair(m, m)] = 1.toDouble()
            right.add(Right(m, g2(y(j.toDouble(), hj))))
        }
        for (i in 1 until Nx) {
            val j = 1
            val m = j * l + i + 1
            val key = Pair(m, m - l)
            val coef = coefs[key]!!
            right.find { it.m == m }!!.value -= g3(x(i.toDouble(), hi)) * coef
            coefs.remove(key)
        }
        for (j in 1..Ny) {
            val i = 1
            val m = j * l + i + 1
            val key = Pair(m, m - 1)
            val coef = coefs[key]!!
            right.find { it.m == m }!!.value -= g1(y(j.toDouble(), hj)) * coef
            coefs.remove(key)
        }
        for (j in 1..Ny) {
            val i = Nx - 1
            val m = j * l + i + 1
            val key = Pair(m, m + 1)
            val coef = coefs[key]!!
            right.find { it.m == m }!!.value -= g2(y(j.toDouble(), hj)) * coef
            coefs.remove(key)
        }
        return Pair(res.first.filter { it.key.first >= it.key.second }, res.second)
    }
}
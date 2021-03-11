package tests

import model.Coef
import model.Right

interface TestProvider {
    fun a(): Double
    fun b(): Double
    fun c(): Double
    fun d(): Double

    fun x(i: Double, hx: Double): Double
    fun y(i: Double, hy: Double): Double
    fun u(x: Double, y: Double): Double

    fun getTestData(Hi: Double, Hj: Double, l: Int): Pair<List<Coef>, List<Right>>
}
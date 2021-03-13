import annotations.Coefs
import annotations.Vector
import model.Coef
import model.MutablePair
import tests.TestProvider
import kotlin.math.sqrt

fun vectorBinary(@Vector a: List<Double>, @Vector b: List<Double>, operator: (Double, Double) -> Double): MutableList<Double> {
    val res = MutableList(a.size) { 0.toDouble() }
    for (i in a.indices) {
        res[i] = operator.invoke(a[i], b[i])
    }
    return res
}

fun solveTransp(@Coefs coefs: List<Coef>, @Vector right: List<Double>, l: Int): MutableList<Double> {
    val res = MutableList(right.size) { 0.toDouble() }
    for (i in (right.size - 1) downTo 0) {
        when (i) {
            right.size - 1 -> {
                res[i] = right[i] / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef
            }
            in right.size - 1 - l until right.size - 1 -> {
                val left = coefs.find { it.ir == i + 2 && it.ic == i + 1 }?.coef ?: 0.toDouble()
                res[i] = (right[i] - left * res[i + 1]) / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef
            }
            else -> {
                val left = coefs.find { it.ir == i + 2 && it.ic == i + 1 }?.coef ?: 0.toDouble()
                val farLeft = coefs.find { it.ir == i + l + 1 && it.ic == i + 1 }?.coef ?: 0.toDouble()
                res[i] =
                    (right[i] - left * res[i + 1] - farLeft * res[i + l]) / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef
            }
        }
    }
    return res
}

fun solveCommon(@Coefs coefs: List<Coef>, @Vector right: List<Double>, l: Int): MutableList<Double> {
    val res = mutableListOf<Double>()
    for (i in right.indices) {
        when (i) {
            0 -> {
                res.add(right[i] / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef)
            }
            in 1 until l -> {
                val left = coefs.find { it.ir == i + 1 && it.ic == i }?.coef ?: 0.toDouble()
                res.add((right[i] - left * res[i - 1]) / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef)
            }
            else -> {
                val left = coefs.find { it.ir == i + 1 && it.ic == i }?.coef ?: 0.toDouble()
                val farLeft = coefs.find { it.ir == i + 1 && it.ic == i - l + 1 }?.coef ?: 0.toDouble()
                res.add((right[i] - left * res[i - 1] - farLeft * res[i - l]) / coefs.find { it.ir == i + 1 && it.ic == i + 1 }!!.coef)
            }
        }
    }
    return res
}

fun scalarMultiply(@Vector a: List<Double>, @Vector b: List<Double>): Double {
    var res: Double = 0.toDouble()
    for (i in a.indices) {
        res += a[i] * b[i]
    }
    return res
}

fun multiply(@Coefs l: List<Coef>, @Vector y: List<Double>): MutableList<Double> {
    val res = mutableListOf<Double>()
    for (i in y.indices) {
        val rowCoefs = l.filter { it.ir == i + 1 }
        var sum = 0.toDouble()
        rowCoefs.forEach {
            sum += it.coef * y[it.ic - 1]
        }
        val colCoefs = l.filter { it.ic == i + 1 && it.ic != it.ir }
        colCoefs.forEach {
            sum += it.coef * y[it.ir - 1]
        }
        res.add(sum)
        sum = 0.toDouble()
    }
    return res
}

fun gradientMethod(testProvider: TestProvider, l : Int, Nx: Int, Ny: Int): Pair<List<Double>, Int> {
    val testData = testProvider.getTestData(Nx, Ny, l)
    @Coefs val coefs = testData.first
    // index of answ need to be incremented
    @Vector val answ = testData.second.sortedBy { it.m }.map { it.value }.toMutableList()

    val newCoefs = mutableListOf<Coef>()

    for (i in 1..(Nx + 1) * (Ny + 1)) {
        val ai = coefs.find { it.ir == i && it.ic == i }!!
        val left = newCoefs.find { it.ir == i - 1 && it.ic == i - 2 }?.coef ?: 0.toDouble()
        val farLeft = newCoefs.find { it.ir == i - l && it.ic == i - 2 * l }?.coef ?: 0.toDouble()
        var newCoef = ai.coef - left * left - farLeft * farLeft
        val bi = coefs.find { it.ir == i && it.ic == i - 1 }
        val ci = coefs.find { it.ir == i && it.ic == i - l }
        if (newCoef < 1) {
            val multiplier = (left * left + farLeft * farLeft + 1) / ai.coef
            answ[i - 1] = answ[i - 1] * multiplier
            ai.coef = ai.coef * multiplier
            newCoef = ai.coef - left * left - farLeft * farLeft
        }
        bi?.let {
            newCoefs.add(Coef(it.coef / sqrt(newCoef), i, i - 1))
        }

        ci?.let {
            newCoefs.add(Coef(it.coef / sqrt(newCoef), i, i - l))
        }
        newCoefs.add(Coef(sqrt(newCoef), i, i))
    }


    val x0 = MutablePair(MutableList((Nx + 1) * (Ny + 1)) { 0.toDouble() },
        MutableList((Nx + 1) * (Ny + 1)) { 0.toDouble() })
    val Ax0 = multiply(coefs, x0.first)
    val r0 = MutablePair<MutableList<Double>, MutableList<Double>>(
        vectorBinary(answ, Ax0, Double::minus),
        mutableListOf()
    )
    val y0 = MutablePair<MutableList<Double>, MutableList<Double>>(
        solveCommon(newCoefs, r0.first, l),
        mutableListOf()
    )
    val w0 = MutablePair<MutableList<Double>, MutableList<Double>>(
        solveTransp(newCoefs, y0.first, l),
        mutableListOf()
    )
    val s1 = MutablePair(
        w0.first,
        MutableList((Nx + 1) * (Ny + 1)) { 0.toDouble() })

    var k = 0
    for (j in 0..1000) {
        //println("iteration $j")
        if (j > 0) {
            w0.first = w0.second
            r0.first = r0.second
            y0.first = y0.second
            x0.first = x0.second
            s1.first = s1.second
        }
        val alphaK = scalarMultiply(w0.first, r0.first) / scalarMultiply(multiply(coefs, s1.first), s1.first)
        x0.second = vectorBinary(x0.first, s1.first.map { it * alphaK }, Double::plus)
        r0.second =
            vectorBinary(r0.first, multiply(coefs, s1.first).map { it * alphaK }, Double::minus)
        if (sqrt(scalarMultiply(r0.second, r0.second) / scalarMultiply(answ, answ)) > EPSILON) {
            y0.second = solveCommon(newCoefs, r0.second, l)
            w0.second = solveTransp(newCoefs, y0.second, l)
            val beta = scalarMultiply(w0.second, r0.second) / scalarMultiply(w0.first, r0.first)
            s1.second = vectorBinary(w0.second, s1.first.map { it * beta }, Double::plus)
        } else {
            k = j
            break
        }
    }
    return Pair(x0.second, k)
}

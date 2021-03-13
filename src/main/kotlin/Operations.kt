import annotations.Coefs
import annotations.Vector
import model.MutablePair
import model.Right
import tests.TestProvider
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun vectorBinary(
    @Vector a: List<Double>,
    @Vector b: List<Double>,
    operator: (Double, Double) -> Double
): MutableList<Double> {
    val res = MutableList(a.size) { 0.toDouble() }
    for (i in a.indices) {
        res[i] = operator.invoke(a[i], b[i])
    }
    return res
}

fun solveTransp(@Coefs coefs: Map<Pair<Int, Int>, Double>, @Vector right: List<Double>, l: Int): MutableList<Double> {
    val res = MutableList(right.size) { 0.toDouble() }
    for (i in (right.size - 1) downTo 0) {
        when (i) {
            right.size - 1 -> {
                res[i] = right[i] / coefs[Pair(i + 1, i + 1)]!!
            }
            in right.size - 1 - l until right.size - 1 -> {
                val left = coefs[Pair(i + 2, i + 1)] ?: 0.toDouble()
                res[i] = (right[i] - left * res[i + 1]) / coefs[Pair(i + 1, i + 1)]!!
            }
            else -> {
                val left = coefs[Pair(i + 2, i + 1)] ?: 0.toDouble()
                val farLeft = coefs[Pair(i + l + 1, i + 1)] ?: 0.toDouble()
                res[i] =
                    (right[i] - left * res[i + 1] - farLeft * res[i + l]) / coefs[Pair(i + 1, i + 1)]!!
            }
        }
    }
    return res
}

fun solveCommon(@Coefs coefs: Map<Pair<Int, Int>, Double>, @Vector right: List<Double>, l: Int): MutableList<Double> {
    val res = mutableListOf<Double>()
    for (i in right.indices) {
        when (i) {
            0 -> {
                res.add(right[i] / coefs[Pair(i + 1, i + 1)]!!)
            }
            in 1 until l -> {
                val left = coefs[Pair(i + 1, i)] ?: 0.toDouble()
                res.add((right[i] - left * res[i - 1]) / coefs[Pair(i + 1, i + 1)]!!)
            }
            else -> {
                val left = coefs[Pair(i + 1, i)] ?: 0.toDouble()
                val farLeft = coefs[Pair(i + 1, i - l + 1)] ?: 0.toDouble()
                res.add((right[i] - left * res[i - 1] - farLeft * res[i - l]) / coefs[Pair(i + 1, i + 1)]!!)
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

fun multiply(@Coefs l: Map<Pair<Int, Int>, Double>, @Vector y: List<Double>): MutableList<Double> {
    val res = mutableListOf<Double>()
    for (i in y.indices) {
        val rowCoefs = l.filter { it.key.first == i + 1 }
        var sum = 0.toDouble()
        rowCoefs.forEach {
            sum += it.value * y[it.key.second - 1]
        }
        val colCoefs = l.filter { it.key.second == i + 1 && it.key.second != it.key.first }
        colCoefs.forEach {
            sum += it.value * y[it.key.first - 1]
        }
        res.add(sum)
        sum = 0.toDouble()
    }
    return res
}

fun gradientMethod(testProvider: TestProvider, l: Int, Nx: Int, Ny: Int): Pair<List<Double>, Int> {
    val testData = testProvider.getTestData(Nx, Ny, l)
    @Coefs val coefs = testData.first.toMutableMap()
    // index of answ need to be incremented
    @Vector val answ = testData.second.sortedBy { it.m }.map { it.value }.toMutableList()

    val newCoefs = mutableMapOf<Pair<Int, Int>, Double>()

    for (i in 1..(Nx + 1) * (Ny + 1)) {
        val ai = coefs[Pair(i, i)]!!
        val left = newCoefs[Pair(i - 1, i - 2)] ?: 0.toDouble()
        val farLeft = newCoefs[Pair(i - l, i - 2 * l)] ?: 0.toDouble()
        var newCoef = ai - left * left - farLeft * farLeft
        val bi = coefs[Pair(i, i - 1)]
        val ci = coefs[Pair(i, i - l)]
        if (newCoef < 1) {
            val multiplier = (left * left + farLeft * farLeft + 1) / ai
            answ[i - 1] = answ[i - 1] * multiplier
            coefs[Pair(i, i)] = ai * multiplier
            newCoef = ai * multiplier - left * left - farLeft * farLeft
        }
        bi?.let {
            newCoefs[Pair(i, i - 1)] = it / sqrt(newCoef)
        }

        ci?.let {
            newCoefs[Pair(i, i - l)] = it / sqrt(newCoef)
        }
        newCoefs[Pair(i, i)] = sqrt(newCoef)
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
    for (j in 0..400) {
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

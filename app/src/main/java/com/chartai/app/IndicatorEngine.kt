package com.chartai.app

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class AnalysisResult(
    val signal: String,
    val score: Int,
    val rsi: Double,
    val macd: String,
    val ichimoku: String,
    val psar: String,
    val entry: Double,
    val stop: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double
)

object IndicatorEngine {

    fun analyze(prices: List<Double>): AnalysisResult {
        require(prices.size >= 30)

        val rsi = rsi(prices, 14)
        val ema12 = ema(prices, 12)
        val ema26 = ema(prices, 26)
        val macdValue = ema12.last() - ema26.last()
        val macdSignal = if (macdValue >= 0) "Bullish 🟢" else "Bearish 🔴"

        val high = prices.maxOrNull() ?: prices.last()
        val low = prices.minOrNull() ?: prices.last()
        val last = prices.last()

        val conversion = (prices.takeLast(9).maxOrNull()!! + prices.takeLast(9).minOrNull()!!) / 2.0
        val base = (prices.takeLast(26).maxOrNull()!! + prices.takeLast(26).minOrNull()!!) / 2.0
        val cloud = (conversion + base) / 2.0
        val ichi = if (last >= cloud) "Above Cloud 🟢" else "Below Cloud 🔴"

        val sar = prices.takeLast(10).minOrNull() ?: last
        val psar = if (last >= sar) "Bullish 🟢" else "Bearish 🔴"

        var score = 0
        if (rsi >= 50) score++ else score--
        if (macdValue >= 0) score++ else score--
        if (last >= cloud) score++ else score--
        if (last >= sar) score++ else score--

        val signal = when {
            score >= 3 -> "STRONG BUY 🟢"
            score >= 1 -> "BUY 🟢"
            score <= -3 -> "STRONG SELL 🔴"
            score <= -1 -> "SELL 🔴"
            else -> "NEUTRAL 🟡"
        }

        val risk = max(last * 0.008, abs(high - low) * 0.08)
        val stop = if (score >= 0) last - risk else last + risk
        val tp1 = if (score >= 0) last + risk else last - risk
        val tp2 = if (score >= 0) last + risk * 2 else last - risk * 2
        val tp3 = if (score >= 0) last + risk * 3 else last - risk * 3

        return AnalysisResult(
            signal, score, rsi, macdSignal, ichi, psar,
            last, stop, tp1, tp2, tp3
        )
    }

    private fun ema(data: List<Double>, period: Int): List<Double> {
        val k = 2.0 / (period + 1)
        val out = ArrayList<Double>(data.size)
        var prev = data.first()
        out.add(prev)
        for (i in 1 until data.size) {
            prev = data[i] * k + prev * (1 - k)
            out.add(prev)
        }
        return out
    }

    private fun rsi(data: List<Double>, period: Int): Double {
        var gain = 0.0
        var loss = 0.0
        val start = data.size - period
        for (i in start until data.size) {
            val d = data[i] - data[i - 1]
            if (d >= 0) gain += d else loss += -d
        }
        if (loss == 0.0) return 100.0
        val rs = gain / loss
        return 100.0 - (100.0 / (1.0 + rs))
    }
}

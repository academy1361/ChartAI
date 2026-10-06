package com.chartai.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var chartImage: ImageView
    private lateinit var signalText: TextView
    private lateinit var scoreText: TextView
    private lateinit var detailsText: TextView

    private val pickImage = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chartImage = findViewById(R.id.chartImage)
        signalText = findViewById(R.id.signalText)
        scoreText = findViewById(R.id.scoreText)
        detailsText = findViewById(R.id.detailsText)

        val symbolSpinner: Spinner = findViewById(R.id.symbolSpinner)
        val timeframeSpinner: Spinner = findViewById(R.id.timeframeSpinner)

        symbolSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("BTC/USDT", "ETH/USDT", "BNB/USDT", "SOL/USDT", "XRP/USDT")
        )

        timeframeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("5m", "15m", "1h", "4h", "1D")
        )

        findViewById<TextView>(R.id.imageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, pickImage)
        }

        findViewById<Button>(R.id.analyzeButton).setOnClickListener {
            runAnalysis()
        }
    }

    private fun runAnalysis() {
        val prices = syntheticPrices()
        val result = IndicatorEngine.analyze(prices)

        signalText.text = result.signal
        scoreText.text = "قدرت همگرایی: ${kotlin.math.abs(result.score)}/4"

        detailsText.text = buildString {
            append("RSI(14) → ${"%.2f".format(result.rsi)}\n")
            append("MACD → ${result.macd}\n")
            append("Ichimoku → ${result.ichimoku}\n")
            append("PSAR → ${result.psar}\n\n")
            append("Reference Entry: ${fmt(result.entry)}\n")
            append("Reference SL: ${fmt(result.stop)}\n")
            append("TP1: ${fmt(result.tp1)}\n")
            append("TP2: ${fmt(result.tp2)}\n")
            append("TP3: ${fmt(result.tp3)}\n\n")
            append("Risk/Reward: TP1 → 1:1, TP2 → 2:1, TP3 → 3:1")
        }
    }

    private fun syntheticPrices(): List<Double> {
        val base = 100.0
        val out = ArrayList<Double>()
        var p = base
        repeat(120) {
            p += Random.nextDouble(-1.3, 1.5)
            p = p.coerceAtLeast(20.0)
            out.add(p)
        }
        return out
    }

    private fun fmt(v: Double): String = "%.4f".format(v)

    @Deprecated("Deprecated in Android SDK; retained for simple compatibility.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                chartImage.setImageURI(uri)
                chartImage.visibility = ImageView.VISIBLE
                signalText.text = "تصویر دریافت شد؛ تحلیل عددی محلی آماده اجراست."
            }
        }
    }
}

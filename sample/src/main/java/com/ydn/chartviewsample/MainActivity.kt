package com.ydn.chartviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import com.ydn.chartview.ChartView
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {
    private lateinit var view1: ChartView
    private lateinit var view2: ChartView
    private lateinit var view3: ChartView

    private var timer1 = Timer()
    private var timer2 = Timer()
    private var timer3 = Timer()

    private var prevY1 = 0
    private var prevY2 = 0f
    private var prevY3 = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view1 = findViewById(R.id.chart1)
        view2 = findViewById(R.id.chart2)
        view3 = findViewById(R.id.chart3)

        with(view1) {
            title = "Sample1 (3000 ms) Int"
            range = 30 * 100L
            curveWidth = 2
            chartColorString = "#FF277A"
            titleColorString = "#BABABA"
        }

        with(view2) {
            title = "Sample2 (1500 ms) Float"
            chartColorString = "#6CEB2B"
            textColorString = "#000000"
            titleColorString = "#BABABA"
            range = 15 * 100L
            curveWidth = 3
        }

        with(view3) {
            title = "Sample3 (ALL DATA APPROXIMATION) Float"
            chartColorString = "#00D0DA"
            textColorString = "#000000"
            titleColorString = "#BABABA"
            curveWidth = 3
            showAllData = true
        }
    }

    override fun onPause() {
        super.onPause()

        timer1.cancel()
        timer2.cancel()
        timer3.cancel()

        timer1.purge()
        timer2.purge()
        timer3.purge()

        view1.pause()
        view2.pause()
        view3.pause()
    }

    override fun onResume() {
        super.onResume()

        timer1 = Timer()
        timer2 = Timer()
        timer3 = Timer()

        timer1.scheduleAtFixedRate(
            timerTask {
                val x = SystemClock.elapsedRealtime()
                var y = prevY1 + Random().nextInt(100) - 50
                if (y < 0) y = 0

                view1.add(x, y)
                prevY1 = y

            }, 0, 100
        )

        timer2.scheduleAtFixedRate(
            timerTask {
                val x = SystemClock.elapsedRealtime()
                var y = prevY2 + Random().nextFloat() * 10f - 5f
                if (y < 0) y = 0f

                view2.add(x, y)
                prevY2 = y

            }, 0, 10
        )

        timer3.scheduleAtFixedRate(
            timerTask {
                val x = SystemClock.elapsedRealtime()
                var y = prevY3 + Random().nextFloat() * 10f - 5f
                if (y < 0) y = 0f

                view3.add(x, y)
                prevY3 = y

            }, 0, 100
        )
    }
}
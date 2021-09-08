package com.ydn.chartview.graphics

import android.graphics.*
import android.graphics.Color.*
import com.ydn.chartview.chart.Value
import com.ydn.chartview.extensions.roundTo
import kotlin.math.roundToInt

class Drawer {
    var curveWidth = 1
    var precisionX = 0
    var precisionY = 0
    var chartColorString = "#FF5C64"
        set(value) {
            field = value
            chartPaint.color = parseColor(value)
            labelPaint.color = parseColor(value)
            dottedLinePaint.apply {
                val c = parseColor(chartColorString)
                color = argb(50, red(c), green(c), blue(c))
            }
        }
    var textColorString = "#FFFFFF"
        set(value) {
            textPaint.color = parseColor(value)
        }

    var tileColorString = "#909090"
        set(value) {
            titlePaint.color = parseColor(value)
        }

    lateinit var data: ArrayList<Value>
    lateinit var canvas: Canvas
    var range: Double = 0.0
    var maxValue: Double = 0.0

    private val backgroundPaint = Paint()
    private val blackPaint = Paint()
    private val gridPaint = Paint()
    private val chartPaint = Paint()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dottedLinePaint = Paint()
    private val labelPaint = Paint()
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        backgroundPaint.color = parseColor("#202020")
        blackPaint.color = parseColor("#000000")
        gridPaint.color = parseColor("#505050")

        chartPaint.apply {
            color = parseColor(chartColorString)
            isAntiAlias = true
            style = Paint.Style.STROKE
        }

        dottedLinePaint.apply {
            val c = parseColor(chartColorString)
            color = argb(70, red(c), green(c), blue(c))
        }

        textPaint.color = WHITE
        gridTextPaint.color = parseColor("#909090")
        titlePaint.color = gridTextPaint.color

        labelPaint.apply {
            color = parseColor(chartColorString)
            isAntiAlias = true
        }
    }

    fun drawBackground() {
        with(canvas) {
            val w = width + 0f
            val h = height + 0f
            val gap = h.coerceAtMost(w) / 30f

            drawRoundRect(0f, 0f, w, h, 0f, 0f, backgroundPaint)
            drawRoundRect(
                0.5f * gap,
                0.5f * gap,
                w - 0.5f * gap,
                h - 0.5f * gap,
                0f,
                0f,
                blackPaint
            )
            drawRoundRect(gap, gap, w - gap, h - gap, 0f, 0f, backgroundPaint)
        }
    }

    fun drawGrid() {
        with(canvas) {
            val h = height + 0f
            val w = width + 0f
            val gap = h.coerceAtMost(w) / 30f
            val height = h - 2 * gap
            val interval = height.coerceAtMost(w) / 3f

            var y = gap + height - 1
            while (y >= gap) {
                drawLine(gap, y, width - gap, y, gridPaint)
                y -= interval.toInt()
            }

            val bounds = Rect()
            gridTextPaint.textSize = gap * 1.2f

            var x = gap
            var pos = 0.0
            val dx = range / ((w - 2 * gap) / interval)
            var txt = if (precisionX == 0) "0" else 0.0.roundTo(precisionX).toString()

            while (x <= width - gap) {
                drawLine(x, gap, x, gap + height, gridPaint)
                gridTextPaint.getTextBounds(txt, 0, txt.length, bounds)
                drawText(
                    txt,
                    x + gap / 2,
                    gap + height - bounds.height() / 1.5f,
                    gridTextPaint
                )
                x += interval
                pos += dx
                txt = if (precisionX == 0) pos.roundToInt().toString() else pos.roundTo(precisionX)
                    .toString()
            }
        }
    }

    fun drawCurve() {
        if (data.isEmpty()) {
            return
        }

        with(canvas) {
            val w = width - 0f
            val h = height - 0f
            val gap = h.coerceAtMost(w) / 30f

            val pth = Path()
            val startTime = data[0].x

            chartPaint.strokeWidth = (gap / 7.0f) * curveWidth
            val height = h - 2 * gap - chartPaint.strokeWidth

            if (maxValue > 0) {
                pth.moveTo(
                    gap + chartPaint.strokeWidth,
                    (gap + height - height * data[0].y / (maxValue * 1.5f)).toFloat()
                )
            } else {
                pth.moveTo(gap + chartPaint.strokeWidth, gap + height)
            }

            for (i in 1 until data.size) {
                val t0 = data[i - 1].x - startTime
                val t1 = data[i].x - startTime

                val x1 = t1 * (w - 2 * gap) / range
                val x0 = t0 * (w - 2 * gap) / range

                if (gap + x1 + chartPaint.strokeWidth > w - gap || gap + x0 + chartPaint.strokeWidth > w - gap) {
                    continue
                }

                if (maxValue > 0) {
                    pth.quadTo(
                        (gap + x0).toFloat(),
                        (gap + height - height * data[i - 1].y / (maxValue * 1.5f)).toFloat(),
                        (gap + x1).toFloat(),
                        (gap + height - height * data[i].y / (maxValue * 1.5f)).toFloat()
                    )
                } else {
                    pth.quadTo(
                        (gap + x0).toFloat(),
                        gap + height,
                        (gap + x1).toFloat(),
                        gap + height
                    )
                }
            }
            pth.fillType = Path.FillType.EVEN_ODD
            drawPath(pth, chartPaint)
        }
    }

    fun drawDottedLine(value: Double) {
        with(canvas) {
            val w = width
            val h = height
            val gap = h.coerceAtMost(w) / 30f
            val delta = h / 100f

            val height = h - 2 * gap - 1.2f * delta

            var cx = gap - 2 * delta
            var cy = 0f
            while (cx < w - gap) {
                cy = (gap + height - height * value / (maxValue * 1.5f)).toFloat()
                cx += 3.3f * delta

                if (cx + delta / 1.2f < w - gap) {
                    drawCircle(cx, cy, delta / 1.2f, dottedLinePaint)
                }
            }

            val bounds = Rect()
            val txt =
                if (precisionY == 0) value.roundToInt().toString() else value.roundTo(precisionY)
                    .toString()
            textPaint.textSize = gap
            textPaint.getTextBounds(txt, 0, txt.length, bounds)

            drawRoundRect(
                w - 3 * gap - bounds.width(), cy - 1 * gap,
                w - gap, cy + 0.7f * gap, 10f, 10f, labelPaint
            )
            drawText(
                txt,
                w - 2 * gap - bounds.width(),
                cy - 0.15f * gap + bounds.height()/2,
                textPaint
            )
        }
    }

    fun drawTitle(title: String) {
        with(canvas) {
            val w = width
            val h = height
            val bounds = Rect()
            val gap = h.coerceAtMost(w) / 30f

            titlePaint.textSize = gap * 2
            titlePaint.getTextBounds(title, 0, title.length, bounds)

            drawText(
                title,
                2 * gap,
                4 * gap,
                titlePaint
            )
        }
    }
}


package com.ydn.chartview

import android.content.Context
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.ydn.chartview.chart.Chart
import com.ydn.chartview.graphics.Drawer

private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
private const val CHART_STATE_KEY = "CHART_STATE_KEY"


class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    var title = ""
    var range: Number = 30 * 100
        set(value) {
            field = value
            chart.range = value.toDouble()
        }

    @Volatile
    @PublishedApi
    internal var chart = Chart()

    @PublishedApi
    internal val drawer = Drawer()

    var chartColorString = "#FF5C64"
        set(value) {
            field = value
            drawer.chartColorString = value
        }

    var textColorString = "#FFFFFF"
        set(value) {
            drawer.textColorString = value
            field = value
        }

    var titleColorString = "#909090"
        set(value) {
            drawer.tileColorString = value
            field = value
        }

    var curveWidth = 1
        set(value) {
            field = value
            drawer.curveWidth = curveWidth
        }

    var showAllData = false
        set(value) {
            field = value
            chart.showAll = value
        }

    private var canvasThread: CanvasThread? = null

    init {
        setZOrderOnTop(true)
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
    }

    inline fun <reified XType : Number, reified YType : Number> add(x: XType, y: YType) {
        drawer.precisionX = when (XType::class) {
            Byte::class -> 0
            Short::class -> 0
            Int::class -> 0
            Long::class -> 0
            Float::class -> 2
            Double::class -> 4
            else -> throw ClassCastException("Type ${XType::class.simpleName} can not be casted to any known Number type!")
        }
        drawer.precisionY = when (YType::class) {
            Byte::class -> 0
            Short::class -> 0
            Int::class -> 0
            Long::class -> 0
            Float::class -> 2
            Double::class -> 4
            else -> throw ClassCastException("Type ${YType::class.simpleName} can not be casted to any known Number type!")
        }
        chart.add(x.toDouble(), y.toDouble())
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        canvasThread = CanvasThread(holder, this).also {
            it.isRunning = true
            it.start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Ignore
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        pause()
    }

    fun pause() {
        var retry = true
        canvasThread?.isRunning = false
        while (retry) {
            try {
                canvasThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // we will try it again and again...
            }
        }
    }

    fun doDraw(canvas: Canvas) {
        synchronized(chart) {
            with(drawer) {
                this.canvas = canvas
                this.maxValue = chart.max
                this.data = chart.data
                this.range = chart.range

                drawBackground()
                drawGrid()
                drawCurve()
                drawDottedLine(chart.average)
                drawDottedLine(chart.max)
                drawTitle(title)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 400
        val desiredHeight = 400
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                desiredWidth.coerceAtMost(widthSize)
            }
            else -> {
                //Be whatever you want
                desiredWidth
            }
        }

        //Measure Height
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                desiredHeight.coerceAtMost(heightSize)
            }
            else -> {
                //Be whatever you want
                desiredHeight
            }
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()

        synchronized(chart) {
            bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
            bundle.putSerializable(CHART_STATE_KEY, chart)
        }
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        if (newState is Bundle) {
            synchronized(chart) {
                chart = (newState as Bundle).getSerializable(CHART_STATE_KEY) as Chart
            }
            newState = newState.getParcelable(SUPER_STATE_KEY)
        }
        super.onRestoreInstanceState(newState)
    }

    private class CanvasThread(private val holder: SurfaceHolder, private val view: ChartView) :
        Thread() {
        @Volatile
        var isRunning = false

        @Volatile
        private var canvasLocked = false

        override fun run() {
            var canvas: Canvas? = null
            while (isRunning) {
                try {
                    if (!canvasLocked && holder.surface.isValid) {
                        canvas = holder.lockCanvas(null)

                        canvas?.let {
                            canvasLocked = true
                            synchronized(holder) {
                                view.doDraw(it)
                            }
                        }
                    }
                } finally {
                    canvas?.let {
                        if (holder.surface.isValid) {
                            holder.unlockCanvasAndPost(it)
                        }
                        canvasLocked = false
                    }
                }
            }
        }
    }
}


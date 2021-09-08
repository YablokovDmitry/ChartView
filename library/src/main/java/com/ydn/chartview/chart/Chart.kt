package com.ydn.chartview.chart

import java.io.Serializable
import kotlin.collections.ArrayList

private const val MAX_NUMBER_OF_VALUES = 150

class Chart : Serializable {
    @Volatile var data = ArrayList<Value>()
    var range: Double = 30 * 100.0
    var max: Double = 2.0
    var showAll = false
    val average: Double
        get() {
            return if (count > 0) sum/count else 0.0
        }
    private var sum: Double = 0.0
    private var count = 0L

    @Synchronized
    fun add(x: Double, y: Double) {
        max = maxOf(y, max)
        sum += y
        count++

        data.add(Value(x, y))

        val newRange = if (data.isEmpty()) 0.0 else data.last().x - data.first().x
        if (newRange > range) {
            if (showAll) range = newRange * 4 / 3f else  data.removeFirst()
        }

        //Approximate
        if (showAll && data.size > MAX_NUMBER_OF_VALUES) {
            approximate()
        }
    }

    @Synchronized
    private fun approximate() {
        val segmentsNmbr = 30
        val dx = 1f * range / segmentsNmbr
        val freq: Array<Int> = Array(segmentsNmbr) { 0 }

        for (i in data.indices) {
            for (j in 0 until segmentsNmbr) {
                if (data[i].x >= data[0].x + j * dx && data[i].x <= data[0].x + (j + 1) * dx) {
                    freq[j]++
                }
            }
        }
        //Find segment with max count of measurements
        val maxIdx = freq.indexOf(freq.maxOrNull())

        if (freq[maxIdx] > 5) {
            var size = data.size
            var i = 0
            while (i < size) {
                if (data[i].x >= data[0].x + maxIdx * dx && data[i].x <= data[0].x + (maxIdx + 1) * dx) {
                    var j = i + 1
                    while (j < data.size) {
                        if (data[j].x >= data[0].x + (maxIdx + 1) * dx) {
                            break
                        }

                        data[j] = Value(-1.0, -1.0)
                        j += 2
                        size--
                    }
                    data = data.filterIndexed { _, value -> value.x >= 0 } as ArrayList<Value>
                }
                i++
            }
        }
    }
}

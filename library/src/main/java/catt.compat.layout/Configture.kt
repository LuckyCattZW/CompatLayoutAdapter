package catt.compat.layout

import catt.compat.layout.internal.OriginScreenMetrics

object Configture {

    val originMetricsMap: Map<String, OriginScreenMetrics> by lazy {
        mapOf(
            "4:3" to OriginScreenMetrics(
                intArrayOf(4, 3), 1536, 2048, 288.995F, 288.995F, "xhdpi", 320, 8.9F
            ),
            "16:9" to OriginScreenMetrics(intArrayOf(16, 9), 1920, 1080)
        )
    }
}
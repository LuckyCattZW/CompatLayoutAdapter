package catt.compat.layout.internal

import java.util.*

/**
 * 基础屏幕尺寸参数
 */
class OriginScreenMetrics(
    /**屏幕比例*/
    override val screenScale: IntArray,
    /**真实像素宽度*/
    override val realWidthPixel: Int,
    /**真实像素高度*/
    override val realHeightPixel: Int,
    /**真实X轴像素点*/
    override val xdpi: Float = 0F,
    /**真实Y轴像素点*/
    override val ydpi: Float = 0F,
    /**广义密度标签*/
    @IScreenMetrics.Status.DensityLabelClubs
    override val densityLabel: String = IScreenMetrics.M_DPI_LABEL,
    /**广义密度值*/
    @IScreenMetrics.Status.DensityDpiClubs
    override val densityDpi: Int = IScreenMetrics.M_DPI_VALUE,
    /**屏幕尺寸*/
    override val inches: Float = 0F
) : IScreenMetrics {
    private val _TAG: String by lazy { OriginScreenMetrics::class.java.simpleName }

    override fun toString(): String {
        return "$_TAG(" +
                "screenScale=${Arrays.toString(screenScale)}, " +
                "realWidthPixel=$realWidthPixel, " +
                "realHeightPixel=$realHeightPixel, " +
                "xdpi=$xdpi, " +
                "ydpi=$ydpi, " +
                "densityLabel='$densityLabel', " +
                "densityDpi=$densityDpi, " +
                "inches=$inches)"
    }
}
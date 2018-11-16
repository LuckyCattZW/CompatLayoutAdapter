package catt.compat.layout.internal

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import catt.compat.layout.enums.UnitClubs
import catt.compat.layout.enums.Units
import java.util.*
import kotlin.collections.HashMap

/**
 * 当前屏幕参数(需要适配的目标屏幕参数)
 */
class TargetScreenMetrics private constructor() : IScreenMetrics {
    private val _TAG: String by lazy { TargetScreenMetrics::class.java.simpleName }
    private val _outSize: Point by lazy { Point(-1, -1) }
    private val _displayMetrics: DisplayMetrics by lazy { DisplayMetrics() }
    private var defaultDisplay: Display? = null

    var scanMatchConfigMetrics: OriginScreenMetrics? = null
    var scanPropertiesMetricsMap: HashMap<String, OriginScreenMetrics>? = null

    fun initContent(context: Context, property: String) {
        defaultDisplay = (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        scanPropertiesMetricsMap = fetchPropertiesMetricsMap(property)
        scanMatchConfigMetrics = scanPropertiesMetricsMap!![convertScreenScale()]
    }

    inline fun convert(@UnitClubs unit: Int, value: Int): Float = convert(unit, value.toFloat())

    inline fun convert(@UnitClubs unit: Int, value: Float): Float {
        if(value <= 0) return value
        scanMatchConfigMetrics ?: return value

        val os = floatArrayOf(
            scanMatchConfigMetrics!!.realWidthPixel.toFloat(),
            scanMatchConfigMetrics!!.realHeightPixel.toFloat()
        ).apply { sort() }
        val ts = floatArrayOf(realWidthPixel.toFloat(), realHeightPixel.toFloat()).apply { sort() }

        val o = os[0]
        val t = ts[0]

        return when (unit) {
            Units.COMPLEX_UNIT_WIDTH,
            Units.COMPLEX_UNIT_MARGIN_START,
            Units.COMPLEX_UNIT_MARGIN_END,
            Units.COMPLEX_UNIT_LEFT_MARGIN,
            Units.COMPLEX_UNIT_RIGHT_MARGIN,
            Units.COMPLEX_UNIT_PADDING_START,
            Units.COMPLEX_UNIT_PADDING_END,
            Units.COMPLEX_UNIT_PADDING_LEFT,
            Units.COMPLEX_UNIT_PADDING_RIGHT -> {
                t / o * value
            }
            Units.COMPLEX_UNIT_HEIGHT,
            Units.COMPLEX_UNIT_TOP_MARGIN,
            Units.COMPLEX_UNIT_BOTTOM_MARGIN,
            Units.COMPLEX_UNIT_PADDING_TOP,
            Units.COMPLEX_UNIT_PADDING_BOTTOM,
            Units.COMPLEX_UNIT_LINE_SPACING_EXTRA -> {
                t / o * value
            }
            Units.COMPLEX_UNIT_TEXT_SIZE,
            Units.COMPLEX_UNIT_RADIUS,
            Units.COMPLEX_UNIT_BOTTOM_LEFT_RADIUS,
            Units.COMPLEX_UNIT_BOTTOM_RIGHT_RADIUS,
            Units.COMPLEX_UNIT_TOP_LEFT_RADIUS,
            Units.COMPLEX_UNIT_TOP_RIGHT_RADIUS -> {
                t / o * value
            }
            else -> value
        }
    }

    override val screenScale: IntArray get() = calculationScreenScale(realWidthPixel, realHeightPixel)

    override val realWidthPixel: Int
        get() {
            defaultDisplay?.apply {
                getRealSize(_outSize)
                return _outSize.x
            }
            return 0
        }

    override val realHeightPixel: Int
        get() {
            defaultDisplay?.apply {
                getRealSize(_outSize)
                return _outSize.y
            }
            return 0
        }

    override val xdpi: Float
        get() {
            defaultDisplay?.apply {
                getMetrics(_displayMetrics)
                return _displayMetrics.xdpi
            }
            return 0F
        }

    override val ydpi: Float
        get() {
            defaultDisplay?.apply {
                getMetrics(_displayMetrics)
                return _displayMetrics.ydpi
            }
            return 0F
        }

    @IScreenMetrics.Status.DensityDpiClubs
    override val densityDpi: Int
        get() {
            defaultDisplay?.apply {
                getMetrics(_displayMetrics)
                return _displayMetrics.densityDpi
            }
            return 0
        }

    @IScreenMetrics.Status.DensityLabelClubs
    override val densityLabel: String
        get() = IScreenMetrics.convertDensityLabel(densityDpi)

    override val inches: Float get() = calculationInches()


    private fun fetchPropertiesMetricsMap(property: String): HashMap<String, OriginScreenMetrics> {
        if(property.isEmpty()) return HashMap()
        else return when{
            existMultiple(property) /*配置了多个像素比*/->{
                val list = property.split(",")
                val map = HashMap<String, OriginScreenMetrics>()
                for(index in list.indices){
                    val specs = list[index]
                    if(isCorrectFormat(specs)){
                        val specsList = specs.split("x")
                        val scale = calculationScreenScale(specsList[0].toInt(), specsList[1].toInt())
                        val key = "${scale[0]}:${scale[1]}"
                        map[key] = OriginScreenMetrics(scale, specsList[0].toInt(), specsList[1].toInt())
                    }
                }
                map
            }
            !existMultiple(property) && isCorrectFormat(property) /*配置了一个像素比*/->{
                val list = property.split("x")
                val scale = calculationScreenScale(list[0].toInt(), list[1].toInt())
                val map = HashMap<String, OriginScreenMetrics>()
                val key = "${scale[0]}:${scale[1]}"
                map[key] = OriginScreenMetrics(scale, list[0].toInt(), list[1].toInt())
                map
            }
            else -> HashMap()
        }
    }

    private fun existMultiple(property:String) : Boolean = property.indexOf(',', 0, false) != -1

    private fun isCorrectFormat(property:String) : Boolean {
        if(property.indexOf('x', 0, false) != -1){
            val split = property.split("x")
            if(split[0].length in 3..4 && split[1].length in 3..4) return true
        }
        return false
    }


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

    companion object TargetStatic {
        @JvmStatic
        fun get(): TargetScreenMetrics =
            Helper.INSTANCE

        private object Helper {
            val INSTANCE: TargetScreenMetrics by lazy { TargetScreenMetrics() }
        }
    }
}
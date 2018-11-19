package catt.compat.layout.internal

import android.support.annotation.IntDef
import android.support.annotation.StringDef
import java.text.DecimalFormat
import java.util.*

interface IScreenMetrics {
    val screenScale: IntArray
    val realWidthPixel: Int
    val realHeightPixel: Int
    val xdpi: Float
    val ydpi: Float
    @DensityDpiClubs val densityDpi: Int
    @DensityLabelClubs val densityLabel: String
    val inches: Float

    fun maxNumberConventions(m: Int, n: Int): Int {
        var m = m
        var n = n
        while (true) {
            when(m % n == 0){
                true -> return n
                else -> m %= n
            }
            when(n % m == 0){
                true -> return m
                else -> n %= m
            }
        }
    }

    fun calculationScreenScale(w:Int, h:Int):IntArray{
        val o = intArrayOf(w, h)
        o.sortDescending()
        val conventions:Int = maxNumberConventions(o[0], o[1])
        for(index in o.indices){
            o[index] /= conventions
        }
        return o
    }

    fun convertScreenScale() : String = "${screenScale[0]}:${screenScale[1]}"


    /**
     * 计算尺寸
     */
    fun calculationInches(): Float{
        val x = Math.pow(realWidthPixel / xdpi.toDouble(), 2.0)
        val y = Math.pow(realHeightPixel / ydpi.toDouble(), 2.0)
        val screenInches = Math.sqrt(x + y)
        return /*保持小数点后1位*/DecimalFormat("#.0").format(screenInches).toFloat()
    }

    /**真实屏幕像素是否一致*/
    fun equalsRealScreenPixel(w1: Int, h1: Int, w2: Int, h2: Int):Boolean {
        val o = intArrayOf(w1, h1)
        val t = intArrayOf(w2, h2)
        o.sortDescending()
        t.sortDescending()
        for (index in o.indices) {
            if (o[index] != t[index]) false
        }
        return true
    }

    /**
     * 判断屏幕比例是否一致
     */
    fun equalsScale(s1: IntArray, s2: IntArray): Boolean = when (s1.size == s2.size) {
        true -> {
            s1.sortDescending()
            s2.sortDescending()
            for (index in s1.indices) {
                if (s1[index] != s2[index]) false
            }
            true
        }
        else -> false
    }

    /**真实密度是否相等*/
    fun equalsRealDensityDpi(xdpi1: Float, ydpi1: Float, xdpi2: Float, ydpi2: Float): Boolean =
        xdpi1 == xdpi2 && ydpi1 == ydpi2

    /**广义密度是否相等*/
    fun equalsDensityDpi(d1: Int, d2: Int): Boolean = d1 == d2

    companion object Status{
        private val densityLabelMap: HashMap<Int, String> by lazy {
            mapOf(
                L_DPI_VALUE to L_DPI_LABEL,
                M_DPI_VALUE to M_DPI_LABEL,
                H_DPI_VALUE to H_DPI_LABEL,
                XH_DPI_VALUE to XH_DPI_LABEL,
                XXH_DPI_VALUE to XXH_DPI_LABEL,
                XXXH_DPI_VALUE to XXXH_DPI_LABEL
            ) as HashMap<Int, String>
        }

        private val densityDpiMap: HashMap<String, Int> by lazy {
            mapOf(
                L_DPI_LABEL to L_DPI_VALUE,
                M_DPI_LABEL to M_DPI_VALUE,
                H_DPI_LABEL to H_DPI_VALUE,
                XH_DPI_LABEL to XH_DPI_VALUE,
                XXH_DPI_LABEL to XXH_DPI_VALUE,
                XXXH_DPI_LABEL to XXXH_DPI_VALUE
            ) as HashMap<String, Int>
        }

        @StringDef(L_DPI_LABEL, M_DPI_LABEL, H_DPI_LABEL, XH_DPI_LABEL, XXH_DPI_LABEL, XXXH_DPI_LABEL)
        @Retention(AnnotationRetention.SOURCE)
        annotation class DensityLabelClubs

        const val L_DPI_LABEL = "ldpi"
        const val M_DPI_LABEL = "mdpi"
        const val H_DPI_LABEL = "hdpi"
        const val XH_DPI_LABEL = "xhdpi"
        const val XXH_DPI_LABEL = "xxhdpi"
        const val XXXH_DPI_LABEL = "xxxhdpi"

        @IntDef(L_DPI_VALUE, M_DPI_VALUE, H_DPI_VALUE, XH_DPI_VALUE, XXH_DPI_VALUE, XXXH_DPI_VALUE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class DensityDpiClubs

        const val L_DPI_VALUE = 120
        const val M_DPI_VALUE = 160
        const val H_DPI_VALUE = 240
        const val XH_DPI_VALUE = 320
        const val XXH_DPI_VALUE = 480
        const val XXXH_DPI_VALUE = 640

        /**
         * 密度值转密度标签
         */
        fun convertDensityLabel(@DensityDpiClubs densityDpi: Int): String = densityLabelMap[densityDpi] ?: ""

        /**密度标签转密度值*/
        fun convertDensityDpi(@DensityLabelClubs densityLabel: String): Int = densityDpiMap[densityLabel] ?: 0
    }
}
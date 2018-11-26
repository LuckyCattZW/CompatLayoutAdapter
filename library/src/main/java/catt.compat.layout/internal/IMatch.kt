package catt.compat.layout.internal

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import catt.compat.layout.enums.Units

interface IMatch {

    fun View.isAnalyticalFinished(): Boolean {
        var finished = false
        if (width != 0 && measuredWidth != 0) finished = true
        if (height != 0 && measuredHeight != 0) finished = true
        return finished
    }

    fun View.compatMargin(): View{
        if (visibility != View.GONE) {
            layoutParams?.apply {
                if(this@apply is ViewGroup.MarginLayoutParams) convertCompatMargin()
            }
        }
        return this
    }

    fun View.compatMeasuredSize(): View{
        val view = this
        if (visibility != View.GONE) {
            layoutParams?.apply {
                when (this@apply) {
                    is ViewGroup.MarginLayoutParams -> {
                        convertCompatMeasuredWidth(view)
                        convertCompatMeasuredHeight(view)
                    }
                    is ViewGroup.LayoutParams -> {
                        convertCompatMeasuredWidth(view)
                        convertCompatMeasuredHeight(view)
                    }
                }
            }
        }
        return view
    }


    fun View.compatPadding(): View {
        convertCompatPadding()
        return this
    }

    fun View.compatTextParams(): View {
        if (this is TextView) {
            convertCompatTextSize()
            convertCompatLineSpacing()
        }
        return this
    }

    fun View.compatDrawableRadii():View{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (background is GradientDrawable) {
                (background as GradientDrawable).convertCompatCornerRadii()
            }
        }
        return this
    }

    private fun TextView.convertCompatTextSize(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, metrics.getRealPixel(textSize))
    }

    private fun TextView.convertCompatLineSpacing(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        setLineSpacing(metrics.getRealPixel(lineSpacingExtra), lineSpacingMultiplier)
    }

    private fun ViewGroup.LayoutParams.convertCompatMeasuredWidth(view: View, metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        width = when (width) {
            0, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ->
                metrics.getRealPixel(width).toInt()
            else -> metrics.getRealPixel(view.measuredWidth).toInt()
        }
    }

    private fun ViewGroup.LayoutParams.convertCompatMeasuredHeight(view: View, metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        height = when (height) {
            0, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT -> metrics.getRealPixel(height).toInt()
            else -> metrics.getRealPixel(view.measuredHeight).toInt()
        }
    }

    private fun ViewGroup.MarginLayoutParams.convertCompatMargin(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        if (marginEnd > 0 || marginStart > 0) {
            setMargins(
                    metrics.getRealPixel( marginStart).toInt(),
                    metrics.getRealPixel(topMargin).toInt(),
                    metrics.getRealPixel( marginEnd).toInt(),
                    metrics.getRealPixel(bottomMargin).toInt()
            )
        } else {
            setMargins(
                    metrics.getRealPixel(leftMargin).toInt(),
                    metrics.getRealPixel(topMargin).toInt(),
                    metrics.getRealPixel(rightMargin).toInt(),
                    metrics.getRealPixel(bottomMargin).toInt()
            )
        }
    }

    private fun View.convertCompatPadding(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        if (paddingStart > 0 || paddingEnd > 0) setPaddingRelative(
                metrics.getRealPixel(paddingStart).toInt(),
                metrics.getRealPixel(paddingTop).toInt(),
                metrics.getRealPixel(paddingEnd).toInt(),
                metrics.getRealPixel( paddingBottom).toInt()
        )
        else setPadding(
                metrics.getRealPixel(paddingLeft).toInt(),
                metrics.getRealPixel( paddingTop).toInt(),
                metrics.getRealPixel(paddingRight).toInt(),
                metrics.getRealPixel( paddingBottom).toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun GradientDrawable.convertCompatCornerRadii(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        if (cornerRadii != null && cornerRadii.isNotEmpty()) {
            for (index in cornerRadii.indices) {
                cornerRadii[index] = when (index) {
                    0 /*top-left*/ -> metrics.convert(
                            Units.COMPLEX_UNIT_TOP_LEFT_RADIUS,
                            cornerRadii[index]
                    )
                    1 /*top-right*/ -> metrics.convert(
                            Units.COMPLEX_UNIT_TOP_RIGHT_RADIUS,
                            cornerRadii[index]
                    )
                    2 /*bottom-right*/ -> metrics.convert(
                            Units.COMPLEX_UNIT_BOTTOM_RIGHT_RADIUS,
                            cornerRadii[index]
                    )
                    3 /*bottom-left*/ -> metrics.convert(
                            Units.COMPLEX_UNIT_BOTTOM_LEFT_RADIUS,
                            cornerRadii[index]
                    )
                    else -> cornerRadii[index]
                }
            }
        } else {
            cornerRadius = metrics.convert(Units.COMPLEX_UNIT_RADIUS, cornerRadius)
        }
    }
}
package catt.compat.layout.internal

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log.e
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import catt.compat.layout.enums.Units

interface IMatch {
    fun View.compatPixel(): View {
        val view:View = this

        convertCompatPadding()
        if (this is TextView) {
            convertCompatTextSize()
            convertCompatLineSpacing()
        }

        post {
            layoutParams?.apply {
                when(this@apply){
                    is ViewGroup.MarginLayoutParams->{
                        convertCompatMargin()
                        convertCompatWidth(view)
                        convertCompatHeight(view)
                    }
                    is ViewGroup.LayoutParams->{
                        convertCompatWidth(view)
                        convertCompatHeight(view)
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (background is GradientDrawable) {
                (background as GradientDrawable).convertCompatCornerRadii()
            }
        }
        return view
    }

    fun TextView.convertCompatTextSize(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, metrics.convert(Units.COMPLEX_UNIT_TEXT_SIZE, textSize))
    }

    fun TextView.convertCompatLineSpacing(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        setLineSpacing(metrics.convert(Units.COMPLEX_UNIT_LINE_SPACING_EXTRA, lineSpacingExtra), lineSpacingMultiplier)
    }

    fun ViewGroup.LayoutParams.convertCompatWidth(view: View, metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        width = when (width) {
            0, -1, -2 -> metrics.convert(Units.COMPLEX_UNIT_WIDTH, width).toInt()
            else -> metrics.convert(Units.COMPLEX_UNIT_WIDTH, view.measuredWidth).toInt()
        }
    }

    fun ViewGroup.LayoutParams.convertCompatHeight(view: View, metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        height = when (height) {
            0, -1, -2 -> metrics.convert(Units.COMPLEX_UNIT_HEIGHT, height).toInt()
            else -> metrics.convert(Units.COMPLEX_UNIT_HEIGHT, view.measuredHeight).toInt()
        }
    }

    fun ViewGroup.MarginLayoutParams.convertCompatMargin(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        if (marginEnd > 0 || marginStart > 0) {
            setMargins(
                metrics.convert(Units.COMPLEX_UNIT_MARGIN_START, marginStart).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_TOP_MARGIN, topMargin).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_MARGIN_END, marginEnd).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_BOTTOM_MARGIN, bottomMargin).toInt()
            )
        }
        else {
            setMargins(
                metrics.convert(Units.COMPLEX_UNIT_LEFT_MARGIN, leftMargin).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_TOP_MARGIN, topMargin).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_RIGHT_MARGIN, rightMargin).toInt(),
                metrics.convert(Units.COMPLEX_UNIT_BOTTOM_MARGIN, bottomMargin).toInt()
            )
        }
    }

    fun View.convertCompatPadding(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
        if (paddingStart > 0 || paddingEnd > 0) setPaddingRelative(
            metrics.convert(Units.COMPLEX_UNIT_PADDING_START, paddingStart).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_TOP, paddingTop).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_END, paddingEnd).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_BOTTOM, paddingBottom).toInt()
        )
        else setPadding(
            metrics.convert(Units.COMPLEX_UNIT_PADDING_LEFT, paddingLeft).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_TOP, paddingTop).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_RIGHT, paddingRight).toInt(),
            metrics.convert(Units.COMPLEX_UNIT_PADDING_BOTTOM, paddingBottom).toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun GradientDrawable.convertCompatCornerRadii(metrics: TargetScreenMetrics = TargetScreenMetrics.get()) {
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
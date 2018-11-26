package catt.compat.layout.app

import android.content.Context
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.R
import catt.compat.layout.internal.CompatViewInflater
import catt.compat.layout.internal.IMatch
import catt.compat.layout.internal.TargetScreenMetrics
import java.util.*

abstract class CompatLayoutActivity : AppCompatActivity(), LayoutInflater.Factory2, IMatch {
    private val _TAG: String by lazy { CompatLayoutActivity::class.java.simpleName }

    private val delayCompatList: MutableList<View> by lazy { Collections.synchronizedList(ArrayList<View>()) }

    private val compatViewInflater: CompatViewInflater by lazy { CompatViewInflater() }

    private var whetherRootLayout: Boolean = false

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(parent, name, context, attrs)?.scanCompat(attrs)

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(name, context, attrs)?.scanCompat(attrs)

    override fun onCreate(savedInstanceState: Bundle?) {
        whetherRootLayout = false
        printlnMetrics()
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
        super.onCreate(savedInstanceState)
        window.decorView.postViewCompat()
    }

    private fun View.postViewCompat() {
        e(_TAG, "CompatLayoutActivity.postViewCompat: size=${delayCompatList.size}")
        post {
            for (index in delayCompatList.indices) {
                delayCompatList[index].scanMeasuredCompat()
            }
            delayCompatList.clear()
        }
        e(_TAG, "CompatLayoutActivity.postViewCompat: finished")
    }

    override fun setContentView(view: View?) {
        view?.apply {
            newIdentifier = id
            super.setContentView(
                when (newIdentifier > 0) {
                    true -> layoutInflater.inflate(newIdentifier, window.decorView as ViewGroup, false)
                    false -> view
                }
            )
        }
        super.setContentView(view)
    }

    override fun setContentView(layoutResID: Int) {
        newIdentifier = layoutResID
        super.setContentView(
            when (newIdentifier > 0) {
                true -> newIdentifier
                false -> layoutResID
            }
        )
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        view?.apply {
            newIdentifier = id
            super.setContentView(
                when (newIdentifier > 0) {
                    true -> layoutInflater.inflate(newIdentifier, window.decorView as ViewGroup, false)
                    false -> view
                }, params
            )
        }
        super.setContentView(view, params)
    }

    private var newIdentifier: Int = 0
        set(identifier) {
            field = TargetScreenMetrics.get().newIdentifier(identifier)
        }

    private fun printlnMetrics() {
        w(_TAG, "${TargetScreenMetrics.get()}")
    }

    private fun View.scanCompat(attrs: AttributeSet): View {
        if (!whetherRootLayout) {
            for (index in 0 until attrs.attributeCount) {
                whetherRootLayout = when (attrs.getAttributeNameResource(index)) {
                    R.attr.activity_root_layout -> {
                        val attributeValue: String = attrs.getAttributeValue(index)
                        if (attributeValue.isEmpty()) false
                        else attributeValue.toBoolean()
                    }
                    else -> false
                }
                if (whetherRootLayout) break
            }
        } else scanCompat().scanMeasuredCompat()
        return this
    }

    private fun View.scanMeasuredCompat():View{
        return if (isAnalyticalFinished()) compatMargin().compatMeasuredSize()
        else {
            delayCompatList.add(this)
            this
        }
    }

    private fun View.scanCompat():View{
        return compatPadding().compatTextParams().compatDrawableRadii()
    }
}
package catt.compat.layout.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.internal.CompatViewInflater
import catt.compat.layout.internal.IMatch
import catt.compat.layout.internal.TargetScreenMetrics
import java.util.*

abstract class CompatLayoutFragment : Fragment(), LayoutInflater.Factory2, IMatch {
    private val _TAG: String by lazy { CompatLayoutFragment::class.java.simpleName }

    private val delayCompatList: MutableList<View> by lazy { Collections.synchronizedList(ArrayList<View>()) }

    private val compatViewInflater: CompatViewInflater by lazy { CompatViewInflater() }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(parent, name, context, attrs)?.scanCompat()?.scanMeasuredCompat()

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(name, context, attrs)?.scanCompat()?.scanMeasuredCompat()

    fun compatCreateView(layoutId: Int, container: ViewGroup?, attachToRoot: Boolean = false): View? {
        val cloneInContext = layoutInflater.cloneInContext(activity)
        LayoutInflaterCompat.setFactory2(cloneInContext, this@CompatLayoutFragment)
        newIdentifier = layoutId
        return cloneInContext.inflate(when (newIdentifier > 0) {
            true -> newIdentifier
            false -> layoutId
        }, container, attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postViewCompat()
    }


    private fun View.postViewCompat() {
        Log.e(_TAG, "CompatLayoutFragment.postViewCompat: size=${delayCompatList.size}")
        post {
            for (index in delayCompatList.indices) {
                delayCompatList[index].scanMeasuredCompat()
            }
            delayCompatList.clear()
        }
        Log.e(_TAG, "CompatLayoutFragment.postViewCompat: finished")
    }

    private var newIdentifier: Int = 0
        set(identifier) {
            field = TargetScreenMetrics.get().newIdentifier(identifier)
        }

    private inline fun View.scanMeasuredCompat():View{
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
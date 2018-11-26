package catt.compat.layout.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.internal.CompatViewInflater
import catt.compat.layout.internal.IMatch
import catt.compat.layout.internal.TargetScreenMetrics

abstract class CompatLayoutFragment : Fragment(), LayoutInflater.Factory2, IMatch {
    private val _TAG: String by lazy { CompatLayoutFragment::class.java.simpleName }

    private val delayCompatList : ArrayList<View> by lazy { ArrayList<View>() }

    private val compatViewInflater: CompatViewInflater by lazy { CompatViewInflater() }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(parent, name, context, attrs)?.scanCompat()

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(name, context, attrs)?.scanCompat()

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
        post {
            for (index in delayCompatList.indices) {
                delayCompatList[index].scanCompat()
            }
            delayCompatList.clear()
        }
    }

    private var newIdentifier: Int = 0
        set(identifier) {
            field = TargetScreenMetrics.get().newIdentifier(identifier)
        }


    private fun View.scanCompat():View{
        if (isAnalyticalFinished()) compatMargin().compatMeasuredSize()
        else delayCompatList.add(this)
        compatPadding().compatTextParams().compatDrawableRadii()
        return this
    }

}
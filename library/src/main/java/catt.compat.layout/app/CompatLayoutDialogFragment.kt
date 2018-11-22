package catt.compat.layout.app

import android.content.Context
import android.support.v4.app.DialogFragment
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.internal.CompatViewInflater
import catt.compat.layout.internal.IMatch
import catt.compat.layout.internal.TargetScreenMetrics

abstract class CompatLayoutDialogFragment : DialogFragment(), LayoutInflater.Factory2, IMatch {
    private val _TAG: String by lazy { CompatLayoutDialogFragment::class.java.simpleName }

    private val compatViewInflater: CompatViewInflater by lazy { CompatViewInflater() }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(parent, name, context, attrs)?.compatPixel()
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? =
        compatViewInflater.createView(name, context, attrs)?.compatPixel()

    fun compatCreateView(layoutId: Int, container: ViewGroup?, attachToRoot: Boolean = false): View? {
        val cloneInContext = layoutInflater.cloneInContext(activity)
        LayoutInflaterCompat.setFactory2(cloneInContext, this@CompatLayoutDialogFragment)
        newIdentifier = layoutId
        return cloneInContext.inflate(
            when (newIdentifier > 0) {
                true -> newIdentifier
                false -> layoutId
            }, container, attachToRoot
        )
    }

    private var newIdentifier: Int = 0
        set(identifier) {
            field = TargetScreenMetrics.get().newIdentifier(identifier)
        }

}
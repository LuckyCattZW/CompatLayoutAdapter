package catt.compat.layout.internal

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.annotation.NonNull
import android.support.v4.view.ViewCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.*
import android.util.ArrayMap
import android.util.AttributeSet
import android.util.Log.i
import android.view.InflateException
import android.view.View
import android.view.ViewParent
import catt.compat.layout.R
import org.jetbrains.annotations.Nullable

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * This class is responsible for manually inflating our tinted widgets which are used on devices
 * running [KITKAT][android.os.Build.VERSION_CODES.KITKAT] or below. As such, this class
 * should only be used when running on those devices.
 *
 * This class two main responsibilities: the first is to 'inject' our tinted views in place of
 * the framework versions in layout inflation; the second is backport the `android:theme`
 * functionality for any inflated widgets. This include theme inheritance from it's parent.
 */
internal class CompatViewInflater {
    private val mConstructorArgs: Array<Any?> by lazy { arrayOfNulls<Any>(2) }

    fun createView(parent: View?, name: String, @NonNull context: Context, @NonNull attrs: AttributeSet): View? {
        val isPre21 = Build.VERSION.SDK_INT < 21
        // We only want the View to inherit it's context if we're running pre-v21
        val inheritContext = (isPre21 && true && shouldInheritContext(parent as ViewParent))
        return createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true /* Read read app:theme as a fallback at all times for legacy reasons */
        )
    }

    fun createView(name: String, @NonNull context: Context, @NonNull attrs: AttributeSet): View? {
        val isPre21 = Build.VERSION.SDK_INT < 21
        // We only want the View to inherit it's context if we're running pre-v21
        val inheritContext = (isPre21 && true)
        return createView(null, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true /* Read read app:theme as a fallback at all times for legacy reasons */
        )
    }

    private fun shouldInheritContext(parent: ViewParent?): Boolean {
        var p: ViewParent? = parent ?:/*The initial parent is null so just return false*/return false
        while (true) {
            if (p == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true
            } else if (p !is View || ViewCompat.isAttachedToWindow((p as View?)!!)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false
            }
            p = p!!.parent
        }
    }

    private fun createView(parent: View?, name: String, @NonNull context: Context, @NonNull attrs: AttributeSet,
                           inheritContext: Boolean, readAndroidTheme: Boolean, readAppTheme: Boolean): View? {
        val ctx:Context = when {
            //We can emulate Lollipop's android:theme attribute propagating down the view hierarchy  by using the parent's context
            inheritContext && parent != null -> parent.context
            //We then apply the theme on the context, if specified
            readAndroidTheme || readAppTheme -> themifyContext(
                context,
                attrs,
                readAndroidTheme,
                readAppTheme
            )
            else -> context
        }

        val view: View? = when (/*We need to 'inject' our tint aware Views in place of the standard framework versions*/name) {
            "Switch" -> SwitchCompat(ctx, attrs)
            "TextView" -> AppCompatTextView(ctx, attrs)
            "ImageView" -> AppCompatImageView(ctx, attrs)
            "Button" -> AppCompatButton(ctx, attrs)
            "EditText" -> AppCompatEditText(ctx, attrs)
            "Spinner" -> AppCompatSpinner(ctx, attrs)
            "ImageButton" -> AppCompatImageButton(ctx, attrs)
            "CheckBox" -> AppCompatCheckBox(ctx, attrs)
            "RadioButton" -> AppCompatRadioButton(ctx, attrs)
            "CheckedTextView" -> AppCompatCheckedTextView(ctx, attrs)
            "AutoCompleteTextView" -> AppCompatAutoCompleteTextView(ctx, attrs)
            "MultiAutoCompleteTextView" -> AppCompatMultiAutoCompleteTextView(ctx, attrs)
            "RatingBar" -> AppCompatRatingBar(ctx, attrs)
            "SeekBar" -> AppCompatSeekBar(ctx, attrs)
            "View" -> View(ctx, attrs)
            // If the original context does not equal our themed context, then we need to manually
            // inflate it using the name so that android:theme takes effect.
            else -> createViewFromTag(ctx, name, attrs)
        }

        if (view != null) {
            // If we have created a view, check it's android:onClick
            checkOnClickListener(view, attrs)
        }

        return view
    }

    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var n:String = name
        if (n.equals("view", false)) {
            n = attrs.getAttributeValue(null, "class")
        }

        try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            return when{
                n.indexOf('.') == -1 ->  createView(context, n, "android.widget.")
                else ->  createView(context, n, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    /**
     * android:onClick doesn't handle views with a ContextWrapper context. This method
     * backports new framework functionality to traverse the Context wrappers to find a
     * suitable target.
     */
    private fun checkOnClickListener(view: View, attrs: AttributeSet) {
        val context:Context = view.context

        if (!ViewCompat.hasOnClickListeners(view) || context !is ContextWrapper) {
            // Skip our compat functionality if: the view doesn't have an onClickListener,
            // or the Context isn't a ContextWrapper
            return
        }

        val a = context.obtainStyledAttributes(attrs,
            _onClickAttrs
        )
        val handlerName = a.getString(0)
        if (handlerName != null) {
            view.setOnClickListener(
                DeclaredOnClickListener(
                    view,
                    handlerName
                )
            )
        }
        a.recycle()
    }

    @Throws(ClassNotFoundException::class, InflateException::class)
    private fun createView(context: Context, name: String, prefix: String?): View? {
        var constructor: Constructor<out View>? = _constructorMap[name]
        return try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz:Class<out View> = context.classLoader.loadClass(if (prefix != null) prefix + name else name).asSubclass(View::class.java)

                constructor = clazz.getConstructor(*_constructorSignature)
                _constructorMap[name] = constructor
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*mConstructorArgs)
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        }

    }

    /**
     * An implementation of OnClickListener that attempts to lazily load a
     * named click handling method from a parent or ancestor context.
     */
    private class DeclaredOnClickListener(@param:NonNull private val mHostView: View, @param:NonNull private val mMethodName: String) : View.OnClickListener {

        private var mResolvedMethod: Method? = null
        private var mResolvedContext: Context? = null

        override fun onClick(@NonNull v: View) {
            if (mResolvedMethod == null) {
                resolveMethod(mHostView.context, mMethodName)
            }

            try {
                mResolvedMethod!!.invoke(mResolvedContext, v)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(
                        "Could not execute non-public method for android:onClick", e)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(
                        "Could not execute method for android:onClick", e)
            }

        }

        @NonNull
        private fun resolveMethod(@Nullable context: Context?, @NonNull name: String) {
            var ctx: Context? = context
            while (ctx != null) {
                try {
                    if (!ctx.isRestricted) {
                        ctx.javaClass.getMethod(mMethodName, View::class.java).apply {
                            mResolvedMethod = this@apply
                            mResolvedContext = ctx
                            return
                        }
                    }
                } catch (e: NoSuchMethodException) {
                    // Failed to find method, keep searching up the hierarchy.
                }
                ctx = if (ctx is ContextWrapper) ctx.baseContext else /*Can't search up the hierarchy, null out and fail.*/ null
            }

            val id = mHostView.id

            var idText = when (id) {
                View.NO_ID -> ""
                else -> " with id '" + mHostView.context.resources.getResourceEntryName(id) + "'"
            }
            throw IllegalStateException("Could not find method " + mMethodName
                    + "(View) in a parent or ancestor Context for android:onClick "
                    + "attribute defined on view " + mHostView.javaClass + idText)
        }
    }

    internal companion object {
        private val _TAG: String by lazy { CompatViewInflater::class.java.simpleName }
        private val _constructorSignature:Array<Class<out Any>> by lazy {  arrayOf(Context::class.java, AttributeSet::class.java) }
        private val _onClickAttrs:IntArray by lazy { intArrayOf(android.R.attr.onClick) }
        private val _constructorMap:ArrayMap<String, Constructor<out View>> by lazy { ArrayMap<String, Constructor<out View>>() }

        /**
         * Allows us to emulate the `android:theme` attribute for devices before L.
         */
        private fun themifyContext(context: Context, attrs: AttributeSet, useAndroidTheme: Boolean, useAppTheme: Boolean): Context {
            var ctx:Context = context
            var themeId = 0
            ctx.obtainStyledAttributes(attrs, R.styleable.View, 0, 0).apply {
                when{
                    useAndroidTheme ->
                        /*First try reading android:theme if enabled*/
                        themeId = getResourceId(R.styleable.View_android_theme, 0)
                    useAppTheme && themeId == 0 ->
                        /*...if that didn't work, try reading app:theme (for legacy reasons) if enabled*/
                        themeId = getResourceId(R.styleable.View_theme, 0)
                }
                if (themeId != 0) {
                    i(_TAG, "app:theme is now deprecated. " + "Please move to using android:theme instead.")
                }
            }.recycle()
            if (themeId != 0 && (ctx !is ContextThemeWrapper || (ctx is ContextThemeWrapper && ctx.themeResId != themeId))) {
                // If the context isn't a ContextThemeWrapper, or it is but does not have
                // the same theme as we need, wrap it in a new wrapper
                ctx = ContextThemeWrapper(ctx, themeId)
            }
            return ctx
        }
    }
}


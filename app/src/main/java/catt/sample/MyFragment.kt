package catt.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.app.CompatLayoutFragment
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment.*

class MyFragment : CompatLayoutFragment() {
    private val _TAG: String by lazy { MyFragment::class.java.simpleName }
    private val dialogFragment:MyDialogFragment by lazy { MyDialogFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        compatCreateView(R.layout.fragment, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button.setOnClickListener {
            dialogFragment.show(childFragmentManager, "aaaa")
        }
    }

    override fun onDestroyView() {
        this.clearFindViewByIdCache()
        super.onDestroyView()
    }
}
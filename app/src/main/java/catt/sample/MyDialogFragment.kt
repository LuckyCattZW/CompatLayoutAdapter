package catt.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catt.compat.layout.app.CompatLayoutDialogFragment
import kotlinx.android.synthetic.*

class MyDialogFragment : CompatLayoutDialogFragment(){
    private val _TAG: String by lazy { MyDialogFragment::class.java.simpleName }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        compatCreateView(R.layout.dialog, container)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onDestroyView() {
        this.clearFindViewByIdCache()
        super.onDestroyView()
    }
}
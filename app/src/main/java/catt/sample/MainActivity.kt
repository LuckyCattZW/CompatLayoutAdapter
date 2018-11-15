package catt.sample

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import catt.compat.layout.app.CompatLayoutActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : CompatLayoutActivity() {
    private val _TAG: String by lazy { MainActivity::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_MAIN
                component = ComponentName(this@MainActivity, SubMainActivity::class.java)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }

        MyRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = MyAdapter()
        }

        supportFragmentManager.beginTransaction().add(MyFrameLayout.id, MyFragment()).commitAllowingStateLoss()
        Log.e(_TAG, "applicationContext.packageResourcePath=${applicationContext.packageResourcePath}")
    }

    override fun onDestroy() {
        this.clearFindViewByIdCache()
        super.onDestroy()
    }

    companion object {
        private class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {

            private val array: IntArray by lazy { intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9) }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
                MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))

            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(R.id.MyItemTextView).apply {
                    text = "$position"
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#333333"))
                }
            }

            override fun getItemCount(): Int = array.size

        }

        private class MyViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }
    }
}

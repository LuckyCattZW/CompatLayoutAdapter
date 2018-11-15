package catt.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.*

class SubMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submain)
    }

    override fun onDestroy() {
        this.clearFindViewByIdCache()
        super.onDestroy()
    }
}
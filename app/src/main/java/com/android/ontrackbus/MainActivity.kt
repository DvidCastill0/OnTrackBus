package com.android.ontrackbus

import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.android.ontrackbus.Login.Login

class MainActivity : AppCompatActivity() {
    private val toolbar: Toolbar? = null
    private val PREGUNTARPERMISOS = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace<Login>(R.id.fl_Container)
            setReorderingAllowed(true)
        }
    }
}
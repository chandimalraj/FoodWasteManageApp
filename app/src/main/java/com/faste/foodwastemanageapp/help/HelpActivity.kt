package com.faste.foodwastemanageapp.help

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.faste.foodwastemanageapp.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val txt1 = findViewById<TextView>(R.id.detail01)
        txt1.paintFlags = txt1.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
    }
}
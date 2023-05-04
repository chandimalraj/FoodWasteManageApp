package com.faste.foodwastemanageapp.journal.journal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.faste.foodwastemanageapp.R

class JournalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        val bundle :Bundle?=intent.extras
        val content = bundle!!.getString("content").toString()

        val journalTxt = findViewById<TextView>(R.id.journalContentTxt)
        journalTxt.text = content

    }

}
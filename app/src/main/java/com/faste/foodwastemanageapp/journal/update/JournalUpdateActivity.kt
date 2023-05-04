package com.faste.foodwastemanageapp.journal.update

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.journal.journals.JournalsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class JournalUpdateActivity : AppCompatActivity() {

    // User data class definition for saving user in database
    data class Journal(
        val uid:String,
        val content: String,

        )

    // Create a reference to the Firebase Storage where you want to store the image
    val storageReference = FirebaseStorage.getInstance().reference.child("images/")
    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Journals")

    val LOG = "LOG"

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_update)

        val bundle :Bundle?=intent.extras
        val id = bundle!!.getString("id").toString()
        val content = bundle!!.getString("content")

        val uid = mAuth.currentUser?.uid.toString()
        val journalTxt = findViewById<EditText>(R.id.journalUpdateText)
        val updateBtn = findViewById<Button>(R.id.journalUpdateBtn)

        journalTxt.setText(content)


        updateBtn.setOnClickListener {
            val setContent = journalTxt.text.toString()
            val journal = Journal(uid,setContent)
            updateJournal(journal,id)

        }

    }

    private fun updateJournal(journal: Journal,id:String) {

        database.child(id).setValue(journal).addOnSuccessListener{
            Toast.makeText(this,"Journal Successfully Updated ", Toast.LENGTH_SHORT).show()
            println("success")
            val intent = Intent( this, JournalsActivity::class.java)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
            println("failed")
        }

    }
}
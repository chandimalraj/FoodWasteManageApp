package com.faste.foodwastemanageapp.journal.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.journal.journals.JournalsActivity
import com.faste.foodwastemanageapp.recipes.add.RecipeAddActivity
import com.faste.foodwastemanageapp.store.store.StoreMyItemsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class JournalAddActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_journal_add)

        val journalTxt = findViewById<EditText>(R.id.journalAddText)
        val saveBtn = findViewById<Button>(R.id.journalSaveBtn)

        val txt = journalTxt.text
        val uid = mAuth.currentUser?.uid.toString()

        saveBtn.setOnClickListener {
            val journal = Journal(
                uid,
                txt.toString(),

            )
            saveJournal(journal)
            println(txt.toString())
        }

    }

    private fun saveJournal(journal: Journal) {

        val key = database.push().key
        database.child(key!!).setValue(journal).addOnSuccessListener{
            Toast.makeText(this,"Journal Successfully Saved ", Toast.LENGTH_SHORT).show()
            println("success")
            val intent = Intent( this@JournalAddActivity, JournalsActivity::class.java)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
            println("failed")
        }

    }


}
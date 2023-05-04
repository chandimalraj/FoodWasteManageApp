package com.faste.foodwastemanageapp.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.store.add.StoreItemAddActivity
import com.google.firebase.database.FirebaseDatabase

class FeedbackAddActivity : AppCompatActivity() {

    data class Feedback(
        val name:String,
        val email:String,
        val message:String
    )

    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Feedbacks")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_add)

        val nameTxt = findViewById<EditText>(R.id.feedbackNameTxt)
        val emailTxt = findViewById<EditText>(R.id.feedbackEmailTxt)
        val messageTxt = findViewById<EditText>(R.id.feedbackDescriptionTxt)
        val sendBtn = findViewById<Button>(R.id.feedbackSendBtn)


        sendBtn.setOnClickListener {

            val name = nameTxt.text.toString()
            val email = emailTxt.text.toString()
            val message = messageTxt.text.toString()

            val feedback = Feedback(
                name,
                email,
                message
            )

            saveFeedback(feedback)

        }

    }

    private fun saveFeedback(feedback: Feedback) {

        val key = database.push().key
        database.child(key!!).setValue(feedback).addOnSuccessListener{
            Toast.makeText(this,"Feedback Successfully Saved ", Toast.LENGTH_SHORT).show()
            println("success")

        }.addOnFailureListener{
            Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
            println("failed")

        }


    }
}
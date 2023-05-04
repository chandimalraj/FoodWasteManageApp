package com.faste.foodwastemanageapp.user.pwdchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.user.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ChangepasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        //firebase instance
        val auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.changePwdEmail)
        val nextBtn = findViewById<Button>(R.id.changePwdNextBtn)

        val myClickListener = View.OnClickListener {

            val email = email.text.toString()

            if(email.isEmpty()){

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener

            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password Reset Email has been sent to your email", Toast.LENGTH_SHORT).show()
                        val intent = Intent( this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error password Reset Email", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        nextBtn.setOnClickListener(myClickListener)
    }
}
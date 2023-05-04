package com.faste.foodwastemanageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.faste.foodwastemanageapp.store.store.StoreActivity
import com.faste.foodwastemanageapp.user.login.LoginActivity
import com.faste.foodwastemanageapp.user.register.RegisterActivity
import com.faste.foodwastemanageapp.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logButton = findViewById<Button>(R.id.mainLoginBtn)
        val regButton = findViewById<Button>(R.id.mainRegBtn)
        val buyButton = findViewById<Button>(R.id.mainBuyItemsBtn)

        // Check if user is signed in (non-null) and update UI accordingly
        val currentUser = mAuth.currentUser

        if (currentUser != null) {

            // User is signed in
            // Update UI with the user's information
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra("uid",currentUser.uid)
            startActivity(intent)

        }

        logButton.setOnClickListener { val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        regButton.setOnClickListener {
            val intent = Intent( this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buyButton.setOnClickListener {
            val intent = Intent( this, StoreActivity::class.java)
            startActivity(intent)
        }

    }



}
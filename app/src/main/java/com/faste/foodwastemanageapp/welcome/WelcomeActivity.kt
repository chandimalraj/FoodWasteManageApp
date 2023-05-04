package com.faste.foodwastemanageapp.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.user.login.LoginActivity
import com.faste.foodwastemanageapp.recipes.recipes.RecipesActivity
import com.faste.foodwastemanageapp.store.store.StoreActivity
import com.faste.foodwastemanageapp.user.account.UserAccountActivity
import com.faste.foodwastemanageapp.user.update.UpdateUserActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid.toString()

        val searchRecipeBtn = findViewById<Button>(R.id.welcomeSearchBtn)
        val logOutBtn = findViewById<Button>(R.id.welcomeLogoutBtn)
        val updateBtn = findViewById<Button>(R.id.welcomeUpdateBtn)
        val viewBtn = findViewById<Button>(R.id.welcomeViewAccountBtn)
        val buyBtn = findViewById<Button>(R.id.welcomeBuyBtn)

        searchRecipeBtn.setOnClickListener {
            val intent = Intent( this, RecipesActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }

        logOutBtn.setOnClickListener {

            val currentuer = mAuth.currentUser

            if(currentuer!=null){
                mAuth.signOut()
                val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                googleSignInClient.signOut()
                val intent = Intent( this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        updateBtn.setOnClickListener {
            val intent = Intent( this, UpdateUserActivity::class.java)
            startActivity(intent)
        }

        viewBtn.setOnClickListener {
            val intent = Intent( this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        buyBtn.setOnClickListener {
            val intent = Intent( this, StoreActivity::class.java)
            startActivity(intent)
        }
    }






}
package com.faste.foodwastemanageapp.user.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.faste.foodwastemanageapp.MainActivity
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.feedback.FeedbackAddActivity
import com.faste.foodwastemanageapp.help.HelpActivity
import com.faste.foodwastemanageapp.journal.journals.JournalsActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.squareup.picasso.Picasso

class UserAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid.toString()
        val username = mAuth.currentUser?.displayName.toString()
        val email = mAuth.currentUser?.email.toString()
        val imgUrl = mAuth.currentUser?.photoUrl.toString()

        val nameTxt = findViewById<TextView>(R.id.userAccountName)
        val emailTxt = findViewById<TextView>(R.id.userAccountEmail)
        val fbackBtn = findViewById<Button>(R.id.userAccountFBBtn)
        val journalBtn = findViewById<Button>(R.id.userAccountJournalBtn)
        val imgView = findViewById<ImageView>(R.id.userAccountImgView)
        val helpBtn = findViewById<Button>(R.id.userAccountHelpBtn)
        val deleteBtn = findViewById<Button>(R.id.userAccountDeleteBtn)

        Picasso.get().load(imgUrl).into(imgView)

        nameTxt.text = username
        emailTxt.text = email

        fbackBtn.setOnClickListener {
            val intent = Intent( this, FeedbackAddActivity::class.java)
            startActivity(intent)
        }
        journalBtn.setOnClickListener {
            val intent = Intent( this, JournalsActivity::class.java)
            startActivity(intent)
        }
        helpBtn.setOnClickListener {
            val intent = Intent( this, HelpActivity::class.java)
            startActivity(intent)
        }

        deleteBtn.setOnClickListener {

            val user = mAuth.currentUser

            // Check if the user is signed in with Google Sign-In
            val isSignedInWithGoogle = user?.let {
                for (info in it.providerData) {
                    if (GoogleAuthProvider.PROVIDER_ID == info.providerId) {
                        // User is signed in with Google Sign-In
                        return@let true
                    }
                }
                // User is not signed in with Google Sign-In
                false
            } ?: false // User is not signed in

            if (isSignedInWithGoogle) {
                // User is signed in with Google Sign-In
                // Create a GoogleAuthProvider credential
                // Get the signed-in user's GoogleSignInAccount
                val account = GoogleSignIn.getLastSignedInAccount(this)
                // Use the credential as needed
                val googleIdToken = account?.idToken
                // Create a GoogleAuthProvider credential
                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                // Sign in with the Google credential
                mAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        // User signed in successfully
                        val user = it.user
                        // Delete the user's account

                        if (user != null) {
                            user.delete()
                                .addOnSuccessListener {
                                    // Account deleted successfully
                                    Toast.makeText(this,"Account deleted successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent( this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { exception ->
                                    // Handle errors
                                    Toast.makeText(this,"Account not deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                        }

                    }
                    .addOnFailureListener { exception ->
                        // Handle errors
                    }

            } else {
                // User is not signed in with Google Sign-In

                // Delete the user if they exist
                mAuth.currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User account deleted successfully
                            Toast.makeText(this,"Account deleted successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent( this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // An error occurred while deleting the user account
                            Toast.makeText(this,"Account not deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }

    }
}
package com.faste.foodwastemanageapp.user.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.user.pwdchange.ChangepasswordActivity
import com.faste.foodwastemanageapp.welcome.WelcomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity(){

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()
    val RC_SIGN_IN = 123
    val TAG = "TAG"

     class User(
        val name: String,
        val email: String,
        val address: String,
        val mobile: String,


    )

    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val logInBtn = findViewById<Button>(R.id.loginLoginBtn)
        val forgotPwd = findViewById<TextView>(R.id.loginForgotPasswordTxt)

        val googleBtn = findViewById<ImageView>(R.id.loginGoogleImg)
        val fbBtn = findViewById<ImageView>(R.id.loginFbImg)
        val twitterBtn = findViewById<ImageView>(R.id.loginTwitterImg)

        //define OnClickListener for register button
        val myLogInClickListener = View.OnClickListener {
           logUser()
                }

       forgotPwd.setOnClickListener {
            val intent = Intent( this, ChangepasswordActivity::class.java)
            startActivity(intent)
        }

        logInBtn.setOnClickListener(myLogInClickListener)

        googleBtn.setOnClickListener{

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("66892489485-2tiepu79nh0jiqjv1fqmpngfjo3jhitf.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            // Start sign-in flow
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

//        fbBtn.setOnClickListener{
//            mAuth.signOut()
//            val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
//            googleSignInClient.signOut()
//        }


        }

//    override fun onBackPressed() {
//        // check if the user is on the screen you want to prevent them from going back to
//        if (this is WelcomeActivity) {
//            // if they are, do nothing (i.e., don't call super.onBackPressed())
//        } else {
//            // if they're not, allow them to go back to the previous screen
//            super.onBackPressed()
//        }
//    }

    // Handle sign-in flow result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Authenticate with Firebase Authentication
                val account = task.getResult(ApiException::class.java)
                googleSignIn(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign-in failed", e)
            }
        }
    }

     //user login
     private fun logUser(){

           val emailInput = findViewById<EditText>(R.id.loginEmail)
           val passwordInput = findViewById<EditText>(R.id.loginPassword)

           //extract values from elements
           val email = emailInput.text.toString()
           val password = passwordInput.text.toString()

           //check  empty fields
           if(email.isEmpty() || password.isEmpty()){

               Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
               return
           }

           mAuth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener(this) { task ->
                   if (task.isSuccessful) {
                       // Sign in success, update UI with the signed-in user's information
                       val intent = Intent( this, WelcomeActivity::class.java)
//                       val user = mAuth.currentUser
//                       if (user != null) {
//                           intent.putExtra("uid",user.uid)
//                       }
                       startActivity(intent)

                   } else {
                       // If sign in fails, display a message to the user.

                       Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                   }
               }

       }

    //user login with google
    private fun googleSignIn(account: GoogleSignInAccount){

          val credential = GoogleAuthProvider.getCredential(account.idToken, null)
          mAuth.signInWithCredential(credential)
              .addOnCompleteListener(this) { task ->
                  if (task.isSuccessful) {
                      saveGoogleSignedUser()
                      Log.d(TAG, "Firebase authentication success")
                      val intent = Intent(this, WelcomeActivity::class.java)
                      val user = mAuth.currentUser
                      if (user != null) {
                          intent.putExtra("uid",user.uid)
                      }
                      startActivity(intent)
                  } else {
                      Log.w(TAG, "Firebase authentication failed", task.exception)
                  }
              }

    }

    private fun saveGoogleSignedUser(){

        val user = mAuth.currentUser
        val uid = user?.uid.toString()
        val name = user?.displayName
        val address = ""
        val mobile = ""
        val email = user?.email.toString()

        val newUser: User
        if(name!=null){
             newUser =  User( name,email,address,mobile)

            val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")

            database.child(uid).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    println(dataSnapshot.exists())

                    if(dataSnapshot.exists()){
                        println(dataSnapshot.exists())
                    }else{
                        database.child(uid).setValue(newUser).addOnSuccessListener {
                            println("Success")
                        }.addOnFailureListener{
                            println("failed")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    // ...
                }
            })

        }




    }


}
package com.faste.foodwastemanageapp.user.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.user.login.LoginActivity
import com.faste.foodwastemanageapp.welcome.WelcomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    //User data class definition for saving user in database
    data class User(
        val name: String,
        val email: String,
        val address: String,
        val mobile: String,
    )
    private lateinit var  database:DatabaseReference
    val RC_SIGN_IN = 123
    val TAG = "TAG"
    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Create a reference to the database location
         database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
//        val usersRef = database.getReference("users")

        //resolve elements of ui
        val name = findViewById<EditText>(R.id.regName)
        val password = findViewById<EditText>(R.id.regPassword)
        val email = findViewById<EditText>(R.id.regEmail)
        val address = findViewById<EditText>(R.id.regAddress)
        val mobile = findViewById<EditText>(R.id.regMobile)
        val regBtn = findViewById<Button>(R.id.regRegBtn)
        val googleBtn = findViewById<ImageView>(R.id.registerGoogleImg)


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

        //define OnClickListener for register button
        val myClickListener = View.OnClickListener {

            //extract values from elements
            val email = email.text.toString()
            val password = password.text.toString()
            val name = name.text.toString()
            val address = address.text.toString()
            val mobile = mobile.text.toString()

            //crate newUser Object for saving user info in database
            val newuser = User(
                name,email,address,mobile
            )

            //check  empty fields
            if(email.isEmpty() || password.isEmpty() || name.isEmpty() || address.isEmpty() || mobile.isEmpty()){

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                        task ->
                    if (task.isSuccessful) {

                        val userUid = mAuth.currentUser?.uid

                        Toast.makeText(this, "CreateUserWithEmail:Success.", Toast.LENGTH_SHORT).show()

                        database.child(userUid.toString()).setValue(newuser).addOnSuccessListener {
                            Toast.makeText(this,"Successfully Saved ", Toast.LENGTH_SHORT).show()
                            println("success")

                            val intent = Intent( this, WelcomeActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener{
                            Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
                            println("failed")
                        }

                        println("created user success")

                    } else {
                        Toast.makeText(this, "CreateUserWithEmail:failed.", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        regBtn.setOnClickListener(myClickListener)

        }

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

        val newUser: LoginActivity.User
        if(name!=null){
            newUser = LoginActivity.User(name, email, address, mobile)

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






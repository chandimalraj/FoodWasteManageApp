package com.faste.foodwastemanageapp.user.update

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.faste.foodwastemanageapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateUserActivity : AppCompatActivity() {

    //User data class definition for saving user in database
    data class User(
        val name: String,
        val email: String,
        val address: String,
        val mobile: String,
    )

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        val name = findViewById<EditText>(R.id.updateNameTxt)
        val address = findViewById<EditText>(R.id.updateAddressTxt)
        val mobile = findViewById<EditText>(R.id.updateMobileTxt)
        val updateBtn = findViewById<Button>(R.id.updateBtn)

        val user = mAuth.currentUser
        val uid = user?.uid.toString()

        database.child(uid).addValueEventListener (
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userName = dataSnapshot.child("name").value.toString()
                    val userAddress = dataSnapshot.child("address").value.toString()
                    val userMobile = dataSnapshot.child("mobile").value.toString()

                    name.setText(userName)
                    address.setText(userAddress)
                    mobile.setText(userMobile)

                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    // ...
                }

    })

        updateBtn.setOnClickListener {

            val name = name.text.toString()
            val address = address.text.toString()
            val mobile = mobile.text.toString()
            val email = user?.email.toString()
            val user = User(name,address,mobile,email)

            database.child(uid).setValue(user).addOnSuccessListener {
                Toast.makeText(this,"Successfully Updated ", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Update Failed ", Toast.LENGTH_SHORT).show()
            }
        }

    }




}
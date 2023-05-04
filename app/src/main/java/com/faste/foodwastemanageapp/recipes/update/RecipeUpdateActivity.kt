package com.faste.foodwastemanageapp.recipes.update

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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.recipes.RecipesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RecipeUpdateActivity : AppCompatActivity() {

    //User data class definition for saving user in database
    data class Recipe(
        val uid:String,
        val name: String,
        val time: String,
        val shelfLife:String,
        val ingredients: String,
        val directions:String,
        val imageUrl: String,
    )

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    var success = false
    lateinit var setImgUrl:String

    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recipes")
    // Create a reference to the Firebase Storage where you want to store the image
    val storageReference = FirebaseStorage.getInstance().reference.child("images/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_update)

        val bundle :Bundle?=intent.extras

        val name = bundle!!.getString("name")
        val imgUrl = bundle!!.getString("img").toString()
        val id = bundle!!.getString("id").toString()
        val time =  bundle!!.getString("time").toString()
        val shelfLife =  bundle!!.getString("shelfLife").toString()
        val ingredients =  bundle!!.getString("ingredients").toString()
        val dir =  bundle!!.getString("directions").toString()

        val uid = mAuth.currentUser?.uid.toString()

        val nameTxt = findViewById<EditText>(R.id.updateRecipeName)
        val timeTxt = findViewById<EditText>(R.id.updateRecipeTime)
        val shelfTxt = findViewById<EditText>(R.id.updateRecipeShelfLife)
        val ingredientsTxt = findViewById<EditText>(R.id.updateRecipeIngredients)
        val imgView = findViewById<ImageView>(R.id.updateRecipeImg)
        val updateBtn = findViewById<Button>(R.id.updateRecipeBtn)
        val directionsTxt = findViewById<EditText>(R.id.updateRecipeDirectios)

        setImgUrl = imgUrl

        timeTxt.setText(time)
        shelfTxt.setText(shelfLife)
        ingredientsTxt.setText(ingredients)
        directionsTxt.setText(dir)
        nameTxt.setText(name)
        Picasso.get().load(imgUrl).into(imgView);

        imgView.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
        }

        updateBtn.setOnClickListener {

            val recipeName = nameTxt.text.toString()
            val recipeTime = timeTxt.text.toString()
            val recipeShelf = shelfTxt.text.toString()
            val recipeIngredients = ingredientsTxt.text.toString()
            val directions = directionsTxt.text.toString()

            val recipe = Recipe(uid,recipeName,recipeTime,recipeShelf,recipeIngredients,directions,setImgUrl)
            updateRecipe(recipe,id,uid)
        }
    }

    private fun updateRecipe(recipe: Recipe, key:String,uid:String) {

        database.child(key).setValue(recipe).addOnSuccessListener{
            Toast.makeText(this,"Successfully Updated", Toast.LENGTH_SHORT).show()
            println("success")
            val intent = Intent( this@RecipeUpdateActivity, RecipesActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this,"Update Failed ", Toast.LENGTH_SHORT).show()
            println("failed")

        }

    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection

        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                // getting URI of selected Image
                println(result)
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(applicationContext, imageUri!!)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                val uploadTask = storageReference.child("file/$sd").putFile(imageUri)

                // On success, download the file URL and display it
                uploadTask.addOnSuccessListener {
                    // using glide library to display the image
                    storageReference.child("file/$sd").downloadUrl.addOnSuccessListener {
                        Glide.with(this@RecipeUpdateActivity)
                            .load(it)
                            .into(findViewById<ImageView>(R.id.updateRecipeImg))
                        //Picasso.get().load(it).into(findViewById<ImageView>(R.id.addRecipeImg));
                        success = true
                        setImgUrl = it.toString()

                        Log.e("Firebase", "download passed")
                    }.addOnFailureListener {
                        Log.e("Firebase", "Failed in downloading")
                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }



    }

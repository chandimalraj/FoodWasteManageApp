package com.faste.foodwastemanageapp.recipes.add


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.recipes.RecipesActivity
import com.faste.foodwastemanageapp.welcome.WelcomeActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RecipeAddActivity : AppCompatActivity() {

   //Recipe data class definition for saving user in database
    data class Recipe(
        val uid:String,
        val name: String,
        val time: String,
        val shelfLife:String,
        val ingredients: String,
        val directions:String,
        val imageUrl: String,
    )
      //var success = false
     lateinit var imgUrl:String

    // Create a reference to the Firebase Storage where you want to store the image
    val storageReference = FirebaseStorage.getInstance().reference.child("images/")
    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recipes")

    val LOG = "LOG"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_add)

        val bundle :Bundle?=intent.extras
        val uid = bundle!!.getString("uid")

        val imgView = findViewById<ImageView>(R.id.addRecipeImg)
        val submit = findViewById<Button>(R.id.addRecipeSubmitBtn)
        val name = findViewById<EditText>(R.id.addRecipeName)
        val cookingTime = findViewById<EditText>(R.id.addRecipeTime)
        val shelfLife = findViewById<EditText>(R.id.addRecipeShelfLife)
        val ingredients = findViewById<EditText>(R.id.addRecipeIngredients)
        val directions = findViewById<EditText>(R.id.addRecipeDirections)


        name.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                name.hint = ""

            } else {
                name.hint = "recipe name"

            }
        }
        cookingTime.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                cookingTime.hint = ""
          } else {
                cookingTime.hint = "time in minutes"

                }
            }

        ingredients.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
               ingredients.hint = ""
            } else {
                ingredients.hint = "ingredients"

            }
        }
        submit.setOnClickListener {

            if(::imgUrl.isInitialized){
                val recipeName = name.text.toString()
                val recipeTime = cookingTime.text.toString()
                val shelfLife = shelfLife.text.toString()
                val ingredients = ingredients.text.toString()
                val directions = directions.text.toString()

                val recipe = Recipe( uid.toString() , recipeName , recipeTime,shelfLife,ingredients,directions,imgUrl)
                saveRecipe(recipe,uid.toString())

            }else{

                Toast.makeText(this,"Please select image.. ", Toast.LENGTH_SHORT).show()

            }



        }
           imgView.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
           imagePickerActivityResult.launch(galleryIntent)
        }
    }

    private fun saveRecipe(recipe: Recipe,uid:String) {

        val key = database.push().key
        database.child(key!!).setValue(recipe).addOnSuccessListener{
        Toast.makeText(this,"Successfully Saved ", Toast.LENGTH_SHORT).show()
        println("success")
            val intent = Intent( this@RecipeAddActivity, RecipesActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)

         }.addOnFailureListener{
        Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
        println("failed")

         }

        //database.child("abc").setValue(Recipe("123","name","12","asdd","asdsad"))

      }

    var imagePickerActivityResult: ActivityResultLauncher<Intent> =
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
                        Glide.with(this@RecipeAddActivity)
                            .load(it)
                            .into(findViewById<ImageView>(R.id.addRecipeImg))
                        //Picasso.get().load(it).into(findViewById<ImageView>(R.id.addRecipeImg));
//                        success = true
                        imgUrl = it.toString()
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
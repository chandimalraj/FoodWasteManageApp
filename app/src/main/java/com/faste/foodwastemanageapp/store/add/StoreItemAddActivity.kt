package com.faste.foodwastemanageapp.store.add


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.recipes.RecipesActivity
import com.faste.foodwastemanageapp.store.store.StoreActivity
import com.faste.foodwastemanageapp.store.store.StoreMyItemsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class StoreItemAddActivity : AppCompatActivity() {

    //    //User data class definition for saving user in database
    data class SellItem(
        val uid:String,
        val name: String,
        val category: String,
        val price:String,
        val location:String,
        val description: String,
        val imageUrl: String,
    )
    //     var success = false
    lateinit var imgUrl:String

    // Create a reference to the Firebase Storage where you want to store the image
    val storageReference = FirebaseStorage.getInstance().reference.child("images/")
    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")

    val LOG = "LOG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_item_add)

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        //Create a reference to the database location
        val uid = mAuth.currentUser?.uid.toString()

        val spinner: Spinner = findViewById(R.id.spinner)
        val categories = listOf("Canned Goods", "Dry Goods", "Seasonings","Fresh Food","Cooking and Baking","Condiments")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinner.adapter = adapter

        var category = ""
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                category = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val titleTxt = findViewById<EditText>(R.id.itemAddTitle)
        val priceTxt = findViewById<EditText>(R.id.itemAddPrice)
        val locationTxt = findViewById<EditText>(R.id.itemAddLocation)
        val descriptionTxt = findViewById<EditText>(R.id.itemAddDescription)
        val imageView = findViewById<ImageView>(R.id.itemAddImage)
        val addBtn = findViewById<Button>(R.id.itemAddBtn)
        val viewBtn = findViewById<Button>(R.id.itemViewBtn)

        addBtn.setOnClickListener {

            if(::imgUrl.isInitialized){
                val itemTitle = titleTxt.text.toString()
                val itemCategory= category
                val itemPrice = priceTxt.text.toString()
                val itemLocation = locationTxt.text.toString()
                val itemDescription = descriptionTxt.text.toString()

                val item = SellItem(
                    uid,
                    itemTitle,
                    itemCategory,
                    itemPrice,
                    itemLocation,
                    itemDescription,
                    imgUrl
                )
                saveItem(item)
            }else{

                Toast.makeText(this,"Please select image.. ", Toast.LENGTH_SHORT).show()

            }




        }

        viewBtn.setOnClickListener {
            val intent = Intent(this@StoreItemAddActivity, StoreMyItemsActivity::class.java)
            startActivity(intent)
        }


        imageView.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
        }


    }

    private fun saveItem(item: SellItem) {

        val key = database.push().key
        database.child(key!!).setValue(item).addOnSuccessListener{
            Toast.makeText(this,"Successfully Saved ", Toast.LENGTH_SHORT).show()
            println("success")
            val intent = Intent( this@StoreItemAddActivity, StoreActivity::class.java)
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
                        Glide.with(this@StoreItemAddActivity)
                            .load(it)
                            .into(findViewById<ImageView>(R.id.itemAddImage))
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
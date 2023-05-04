package com.faste.foodwastemanageapp.store.update

import android.annotation.SuppressLint
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
import com.faste.foodwastemanageapp.recipes.update.RecipeUpdateActivity
import com.faste.foodwastemanageapp.store.store.StoreActivity
import com.faste.foodwastemanageapp.store.store.StoreMyItemsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class StoreItemUpdateActivity : AppCompatActivity() {

    //User data class definition for saving user in database
    data class SellItem(
        val uid:String,
        val name: String,
        val category: String,
        val price:String,
        val location:String,
        val description: String,
        val imageUrl: String,
    )

    lateinit var setImgUrl:String

    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()


    //Create a reference to the database location
    val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")
    // Create a reference to the Firebase Storage where you want to store the image
    val storageReference = FirebaseStorage.getInstance().reference.child("images/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_item_update)

        val uid = mAuth.currentUser?.uid.toString()
        val bundle :Bundle?=intent.extras

        val name = bundle!!.getString("name").toString()
        val imgUrl = bundle!!.getString("img").toString()
        val id = bundle!!.getString("id").toString()
        val price =  bundle!!.getString("price").toString()
        val location =  bundle!!.getString("location").toString()
        val description =  bundle!!.getString("description").toString()
        val category =  bundle!!.getString("category").toString()

        val titleTxt = findViewById<EditText>(R.id.itemUpdateTitle)
        val priceTxt = findViewById<EditText>(R.id.itemUpdatePrice)
        val locationTxt = findViewById<EditText>(R.id.itemUpdateLocation)
        val imgView = findViewById<ImageView>(R.id.itemUpdateImage)
        val descTxt = findViewById<EditText>(R.id.itemUpdateDescription)
        val updateBtn =  findViewById<Button>(R.id.myItemUpdateBtn)

        val spinner: Spinner = findViewById(R.id.updateSpinner)
        val categories = listOf("Canned Goods", "Dry Goods", "Seasonings","Fresh Food","Cooking and Baking","Condiments")
        val indexOfCategory = categories.indexOf(category)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
//        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
//            this, R.array.spinner_items, android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(indexOfCategory)
        var categorySet = ""
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                categorySet = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        titleTxt.setText(name)
        priceTxt.setText(price)
        locationTxt.setText(location)
        descTxt.setText(description)
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

            val title = titleTxt.text.toString()
            val category = categorySet
            val price = priceTxt.text.toString()
            val location = locationTxt.text.toString()
            val description = descTxt.text.toString()

            val newItem = SellItem(uid,title,category,price,location,description,imgUrl)

            updateItem(newItem,id)


        }

    }

    private fun updateItem(item: SellItem, key:String) {

        database.child(key).setValue(item).addOnSuccessListener{
            Toast.makeText(this,"Successfully Updated", Toast.LENGTH_SHORT).show()
            println("success")
            val intent = Intent( this@StoreItemUpdateActivity, StoreMyItemsActivity::class.java)
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
                        Glide.with(this@StoreItemUpdateActivity)
                            .load(it)
                            .into(findViewById<ImageView>(R.id.updateRecipeImg))
                        //Picasso.get().load(it).into(findViewById<ImageView>(R.id.addRecipeImg));

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
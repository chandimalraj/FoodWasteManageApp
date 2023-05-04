package com.faste.foodwastemanageapp.store.item

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.faste.foodwastemanageapp.R
import com.squareup.picasso.Picasso

class StoreItemActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_item)

        val bundle :Bundle?=intent.extras
        val id = bundle!!.getString("id")
        val name = bundle!!.getString("name")
        val price = bundle!!.getString("price")
        val location = bundle!!.getString("location")
        val category = bundle!!.getString("category")
        val image = bundle!!.getString("image")
        val description = bundle!!.getString("description")

        val categoryTxt = findViewById<TextView>(R.id.storeItemCategoryTxt)
        val itemNameTxt = findViewById<TextView>(R.id.storeItemNameTxt)
        val imageView = findViewById<ImageView>(R.id.storeItemImgView)
        val itemPriceTxt = findViewById<TextView>(R.id.storeCategoryName)
        val itemLocationTxt = findViewById<TextView>(R.id.storeItemLocation)
        val itemDescriptionTxt = findViewById<TextView>(R.id.storeItemDescriptionTxt)

        categoryTxt.text = category
        itemNameTxt.text = name
        Picasso.get().load(image).into(imageView)
        itemPriceTxt.text = "LKR " + price + ".00"
        itemLocationTxt.text = location
        itemDescriptionTxt.text = description

    }
}
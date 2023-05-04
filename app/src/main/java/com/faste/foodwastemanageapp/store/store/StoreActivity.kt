package com.faste.foodwastemanageapp.store.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.faste.foodwastemanageapp.store.adapter.StoreItemRVAdapter
import com.faste.foodwastemanageapp.store.add.StoreItemAddActivity
import com.faste.foodwastemanageapp.store.category.StoreCategoryActivity
import com.faste.foodwastemanageapp.store.item.StoreItemActivity
import com.faste.foodwastemanageapp.store.model.StoreItemModel
import com.faste.foodwastemanageapp.user.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StoreActivity : AppCompatActivity() {

    // on below line we are creating variables
    // for our swipe to refresh layout,
    // recycler view, adapter and list.
    lateinit var itemRV: RecyclerView
    lateinit var itemRVAdapter: StoreItemRVAdapter
    lateinit var itemList: ArrayList<StoreItemModel>
    private lateinit var  database: DatabaseReference

    private lateinit var progressBar: ProgressBar

    private  val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val addItemBtn = findViewById<Button>(R.id.storeItemAddBtn)
        val storeCategoryBtn = findViewById<Button>(R.id.storeCategoryBtn)
        val searchView = findViewById<SearchView>(R.id.storeItemSearchView)

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        //Create a reference to the database location

        // on below line we are initializing our list
        itemList = ArrayList()

        // our views with their ids.
        itemRV = findViewById(R.id.storeItemRecyclerView)

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        itemRV.layoutManager = layoutManager
        getItemData()
        // on below line we are initializing our adapter
        itemRVAdapter = StoreItemRVAdapter(itemList)

        // on below line we are setting
        // adapter to our recycler view.
        itemRV.adapter = itemRVAdapter


         progressBar = findViewById<ProgressBar>(R.id.storeItemProgress)
        progressBar.visibility = View.VISIBLE
        addItemBtn.setOnClickListener {

            if(mAuth.currentUser!=null){
                val intent = Intent( this, StoreItemAddActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }

        storeCategoryBtn.setOnClickListener {
            val intent = Intent( this, StoreCategoryActivity::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })




    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<StoreItemModel>()
            for (i in itemList) {
                if (i.name.lowercase(Locale.ROOT).startsWith(query.lowercase(Locale.ROOT))) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                itemRVAdapter.setFilteredList(filteredList)
            }
        }
    }


    private fun getItemData(){

        database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")



        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle data change
                    val values = dataSnapshot.value as HashMap<*, *>?
                    itemList.clear()
                    dataSnapshot.children.forEach { child ->
                        val key = child.key // get the key of the current child
                        if (child.value is HashMap<*, *>) {
                            val value = child.value as HashMap<*, *>
                            val name = value["name"] as? String
                            val img = value["imageUrl"] as? String
                            val price = value["price"] as? String
                            val location = value["location"] as? String
                            val category = value["category"] as? String
                            val description = value["description"] as? String
                            if (name != null && img != null) {
                                println(child.key)
                                itemList.add(
                                   StoreItemModel(
                                        key.toString(),
                                        name,price.toString(),
                                        location.toString(), img,category.toString(),description.toString()
                                    )
                                )
                            }
                        }
                    }



                    itemRVAdapter.setOnItemClickListener(object : StoreItemRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@StoreActivity, StoreItemActivity::class.java)
                            intent.putExtra("id",itemList[position].id)
                            intent.putExtra("name",itemList[position].name)
                            intent.putExtra("price",itemList[position].price)
                            intent.putExtra("location",itemList[position].location)
                            intent.putExtra("image",itemList[position].img)
                            intent.putExtra("category",itemList[position].category)
                            intent.putExtra("description",itemList[position].description)
                            startActivity(intent)
                        }

                    })

                    itemRVAdapter.notifyDataSetChanged()
                   // Hide ProgressBar when data is loaded
                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            }
        )

    }
}
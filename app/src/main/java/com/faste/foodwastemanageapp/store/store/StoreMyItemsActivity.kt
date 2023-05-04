package com.faste.foodwastemanageapp.store.store


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.store.adapter.StoreMyItemsRVAdapter
import com.faste.foodwastemanageapp.store.item.StoreItemActivity
import com.faste.foodwastemanageapp.store.model.StoreItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StoreMyItemsActivity : AppCompatActivity() {

    // on below line we are creating variables
    // for our swipe to refresh layout,
    // recycler view, adapter and list.
    lateinit var itemRV: RecyclerView
    lateinit var itemRVAdapter: StoreMyItemsRVAdapter
    lateinit var itemList: ArrayList<StoreItemModel>
    private lateinit var  database: DatabaseReference

    private  val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_my_items)

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid.toString()

        getItemData(uid)
    }

    private fun getItemData(userId:String){

        database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")

        // on below line we are initializing our list
        itemList = ArrayList()

        // our views with their ids.
        itemRV = findViewById(R.id.myItemsViewRecycler)

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 1)

        itemRV.layoutManager = layoutManager

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
                            val uid = value["uid"] as? String
                            val name = value["name"] as? String
                            val img = value["imageUrl"] as? String
                            val price = value["price"] as? String
                            val location = value["location"] as? String
                            val category = value["category"] as? String
                            val description = value["description"] as? String
                            if (name != null && img != null && uid==userId) {
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



                    // on below line we are initializing our adapter
                    itemRVAdapter = StoreMyItemsRVAdapter(itemList)

                    // on below line we are setting
                    // adapter to our recycler view.
                    itemRV.adapter = itemRVAdapter

                    itemRVAdapter.setOnItemClickListener(object : StoreMyItemsRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@StoreMyItemsActivity, StoreItemActivity::class.java)
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

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            }
        )

    }





}
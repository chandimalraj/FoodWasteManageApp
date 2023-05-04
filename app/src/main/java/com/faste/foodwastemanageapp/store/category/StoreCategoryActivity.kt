package com.faste.foodwastemanageapp.store.category


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.store.adapter.StoreCategoryRVAdapter
import com.faste.foodwastemanageapp.store.model.StoreCategoryModel

class StoreCategoryActivity : AppCompatActivity() {

    // on below line we are creating variables
    // for our swipe to refresh layout,
    // recycler view, adapter and list.
    lateinit var categoryRV: RecyclerView
    lateinit var categoryRVAdapter: StoreCategoryRVAdapter
    lateinit var categoryList: ArrayList<StoreCategoryModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_category)

        getCategoryData()

    }

    private fun getCategoryData(){

        // on below line we are initializing our list
        categoryList = ArrayList()

        // our views with their ids.
        categoryRV = findViewById(R.id.storeCategoryRV)

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        categoryRV.layoutManager = layoutManager



        categoryList.add(StoreCategoryModel("Canned Goods",R.drawable.canned))
        categoryList.add(StoreCategoryModel("Dry Goods",R.drawable.dry))
        categoryList.add(StoreCategoryModel("Seasonings",R.drawable.seasonings))
        categoryList.add(StoreCategoryModel("Fresh Food",R.drawable.fresh))
        categoryList.add(StoreCategoryModel("Cooking and Baking",R.drawable.cookbake))
        categoryList.add(StoreCategoryModel("Condiments",R.drawable.condiments))



//         on below line we are initializing our adapter
                    categoryRVAdapter = StoreCategoryRVAdapter(categoryList)

                    // on below line we are setting
                    // adapter to our recycler view.
                    categoryRV.adapter = categoryRVAdapter

                    categoryRVAdapter.setOnItemClickListener(object : StoreCategoryRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@StoreCategoryActivity, StoreCategoryItemsActivity::class.java)
                            intent.putExtra("category",categoryList[position].name)
                            startActivity(intent)
                        }

                    })

                    categoryRVAdapter.notifyDataSetChanged()
                    // Hide ProgressBar when data is loaded



    }
}
package com.faste.foodwastemanageapp.journal.journals


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.journal.adapter.JournalRVAdapter
import com.faste.foodwastemanageapp.journal.add.JournalAddActivity
import com.faste.foodwastemanageapp.journal.journal.JournalActivity
import com.faste.foodwastemanageapp.journal.model.JournalModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JournalsActivity : AppCompatActivity() {

    lateinit var journalRV: RecyclerView
    lateinit var journalRVAdapter: JournalRVAdapter
    lateinit var journalList: ArrayList<JournalModel>
    private lateinit var  database: DatabaseReference
    private  val TAG = "MyActivity"
    //Create an instance of the FirebaseAuth class:
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journals)

        val addBtn = findViewById<FloatingActionButton>(R.id.journalAddBtn)
        val searchView = findViewById<SearchView>(R.id.journalSearchView)

        val uid = mAuth.currentUser?.uid.toString()

        // on below line we are initializing our list
        journalList = ArrayList()

        // our views with their ids.
        journalRV = findViewById(R.id.journalRV)

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 1)
        getJournalsData(uid)
        journalRV.layoutManager = layoutManager


        addBtn.setOnClickListener {

            val intent = Intent(this@JournalsActivity, JournalAddActivity::class.java)
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
            val filteredList = ArrayList<JournalModel>()
            for (i in journalList) {
                if (i.content.lowercase(Locale.ROOT).startsWith(query.lowercase(Locale.ROOT))) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                journalRVAdapter.setFilteredList(filteredList)
            }
        }
    }

    private fun getJournalsData(userId:String){

        database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Journals")

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle data change
                    val values = dataSnapshot.value as HashMap<*, *>?
                    Log.d(TAG, "Value is: $values")


                    journalList.clear()
                    dataSnapshot.children.forEach { child ->
                        val key = child.key // get the key of the current child
                        if (child.value is HashMap<*, *>) {
                            val value = child.value as HashMap<*, *>
                            val uid = value["uid"] as? String
                            val content = value["content"] as? String
                            if ( uid==userId) {
                                journalList.add(
                                   JournalModel(
                                        key.toString(),
                                       content.toString()
                                    )
                                )
                            }
                        }
                    }
                    // on below line we are initializing our adapter
                    journalRVAdapter = JournalRVAdapter(journalList)

                    // on below line we are setting
                    // adapter to our recycler view.
                    journalRV.adapter = journalRVAdapter

                    journalRVAdapter.setOnItemClickListener(object :JournalRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Toast.makeText(this@JournalsActivity,"you clicked on $position" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@JournalsActivity, JournalActivity::class.java)
                            intent.putExtra("content",journalList[position].content)
                            startActivity(intent)
                        }
                    })

                    journalRVAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            }
        )

    }
}
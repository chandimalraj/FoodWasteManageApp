package com.faste.foodwastemanageapp.journal.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.journal.journal.JournalActivity
import com.faste.foodwastemanageapp.journal.model.JournalModel
import com.faste.foodwastemanageapp.journal.update.JournalUpdateActivity
import com.faste.foodwastemanageapp.store.model.StoreItemModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase


class JournalRVAdapter  (

    private var journalList: ArrayList<JournalModel>,


    ) : RecyclerView.Adapter<JournalRVAdapter.JournalViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JournalViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.journal_rv_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return JournalViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        val content = journalList.get(position).content
        holder.journalText.text = content
        val id = journalList.get(position).id

        holder.journalUpdateBtn.setOnClickListener {
            val intent = Intent(holder.itemView.context, JournalUpdateActivity::class.java)
            intent.putExtra("content", content)
            intent.putExtra("id",id)
            holder.itemView.context.startActivity(intent)
        }
        holder.journalDeleteBtn.setOnClickListener {
            //Create a reference to the database location
            val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Journals")

            val objectRef = database.child(id)
            objectRef.removeValue()
        }

        holder.journalText.setOnClickListener {
            val intent = Intent(holder.itemView.context, JournalActivity::class.java)
            intent.putExtra("content",journalList[position].content)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our size of our list
        return journalList.size
    }

    class JournalViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val journalText: TextView = itemView.findViewById(R.id.journalTxt)
        val journalUpdateBtn: FloatingActionButton = itemView.findViewById(R.id.journalUpdateBtn)
        val journalDeleteBtn: FloatingActionButton = itemView.findViewById(R.id.journalDeleteBtn)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun setFilteredList(mList: ArrayList<JournalModel>){
        this.journalList = mList
        notifyDataSetChanged()
    }
}



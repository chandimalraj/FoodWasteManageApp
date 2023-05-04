package com.faste.foodwastemanageapp.store.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.store.model.StoreItemModel
import com.faste.foodwastemanageapp.store.update.StoreItemUpdateActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class StoreMyItemsRVAdapter (

    private val itemList: ArrayList<StoreItemModel>,


    ) : RecyclerView.Adapter<StoreMyItemsRVAdapter.ItemViewHolder>() {

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
        ): ItemViewHolder {
            // this method is use to inflate the layout file
            // which we have created for our recycler view.
            // on below line we are inflating our layout file.
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.store_myitems_rv_item,
                parent, false
            )
            // at last we are returning our view holder
            // class with our item View File.
            return ItemViewHolder(itemView, mListener)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            // on below line we are setting data to our text view and our image view.
            val itemName = itemList.get(position).name
            val itemPrice = itemList.get(position).price
            val itemLocation = itemList.get(position).location
            val itemCategory = itemList.get(position).category
            val itemDescription = itemList.get(position).description
            val imgUrl = itemList.get(position).img
            val itemId = itemList.get(position).id
//        val time = recipeList.get(position)
            holder.itemName.text = itemName
            //holder.courseIV.setImageResource(recipeList.get(position).img)
            val url = itemList.get(position).img
            Picasso.get().load(url).into(holder.itemImageView);

            holder.updateBtn.setOnClickListener {
                val intent = Intent(holder.itemView.context, StoreItemUpdateActivity::class.java)
                intent.putExtra("name",itemName)
                intent.putExtra("img",imgUrl)
                intent.putExtra("price",itemPrice)
                intent.putExtra("location",itemLocation)
                intent.putExtra("category",itemCategory)
                intent.putExtra("description",itemDescription)
                intent.putExtra("id",itemId)
                holder.itemView.context.startActivity(intent)
            }

            holder.deleteBtn.setOnClickListener {
                //Create a reference to the database location
                val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")
                // Create a reference to the Firebase Storage where you want to store the image
                val storageReference = FirebaseStorage.getInstance().reference.child("images/")
                val objectRef = database.child(itemId)
                objectRef.removeValue()

            }
        }

        override fun getItemCount(): Int {
            // on below line we are
            // returning our size of our list
            return itemList.size
        }

        class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
            // on below line we are initializing our course name text view and our image view.
            val itemName: TextView = itemView.findViewById(R.id.itemViewTV)
            val itemImageView: ImageView = itemView.findViewById(R.id.itemViewIV)
            val updateBtn: Button = itemView.findViewById(R.id.itemUpdateBtn)
            val deleteBtn: Button = itemView.findViewById(R.id.itemDeleteBtn)


            init {

                itemView.setOnClickListener{

                    listener.onItemClick(adapterPosition)
                }
            }
        }
    }
package com.example.system.Friends

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class FriendAdapter(private val context: Context) : ListAdapter<Friend, FriendAdapter.FriendViewHolder>(
    FriendDiffCallback()
) {

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendName: TextView = itemView.findViewById(R.id.friendName)

        @SuppressLint("SetTextI18n")
        fun bind(friend: Friend) {
            friendName.text = "${friend.firstName} ${friend.lastName}"

            // Set the OnClickListener for the entire itemView
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, UpdateFriendActivity::class.java).apply {
                    putExtra("FRIEND_ID", friend.id) // Pass friend ID
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }
}

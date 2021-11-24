package com.lazudanizaidan.chatapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterChat constructor(private val listViewType: List<Int>,
                              private val listChat: List<Chat>) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    companion object {
        val VIEW_TYPE_MY_SELF = 1
        val VIEW_TYPE_USER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MY_SELF -> {
                val view = layoutInflater.inflate(R.layout.item_layout_chat_my_self, null)
                ViewHolderChatItemMySelf(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_layout_chat_user, null)
                ViewHolderChatItemUser(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listChat[position]
        listViewType[position].let {
            when (it) {
                VIEW_TYPE_MY_SELF -> {
                    val viewHolderChatItemMySelf = holder as ViewHolderChatItemMySelf
                    viewHolderChatItemMySelf.textViewDateTime.text = chat.dateTime
                    viewHolderChatItemMySelf.textViewMessage.text = chat.message
                }
                else -> {
                    val viewHolderChatUser = holder as ViewHolderChatItemUser
                    viewHolderChatUser.textViewDateTime.text = chat.dateTime
                    viewHolderChatUser.textViewMessage.text = chat.message
                }
            }
        }
    }

    override fun getItemCount(): Int = listChat.size

    override fun getItemViewType(position: Int): Int = listViewType[position]

    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderChatItemMySelf constructor(itemView: View) : ViewHolder(itemView) {

        val textViewDateTime: TextView = itemView.findViewById(R.id.text_view_date_time_item_layout_chat_my_self)
        val textViewMessage: TextView = itemView.findViewById(R.id.text_view_message_item_layout_chat_my_self)

    }

    inner class ViewHolderChatItemUser constructor(itemView: View) : ViewHolder(itemView) {

        val textViewDateTime: TextView = itemView.findViewById(R.id.text_view_date_time_item_layout_chat_user)
        val textViewMessage: TextView = itemView.findViewById(R.id.text_view_message_item_layout_chat_user)

    }

}
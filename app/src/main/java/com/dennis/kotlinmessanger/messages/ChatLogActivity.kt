package com.dennis.kotlinmessanger.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dennis.kotlinmessanger.NewMessageActivity
import com.dennis.kotlinmessanger.R
import com.dennis.kotlinmessanger.User
import com.dennis.kotlinmessanger.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_raw.view.*
import kotlinx.android.synthetic.main.chat_to_raw.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username

//        setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            perfomSendMessage()
        }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else{
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }



    private fun perfomSendMessage(){
        // how do we send message with firebase
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId, System.currentTimeMillis() /
        1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved chat message: ${reference.key}")
                }

    }

    private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("FROM MESSSSSSAGE I OWN"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        adapter.add(ChatFromItem("FROM MESSSSSSAGE I OWN"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        adapter.add(ChatFromItem("FROM MESSSSSSAGE I OWN"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        adapter.add(ChatFromItem("FROM MESSSSSSAGE I OWN"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        adapter.add(ChatFromItem("FROM MESSSSSSAGE I OWN"))
        adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))

        recyclerview_chat_log.adapter = adapter
    }
}

class ChatFromItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_raw
    }
}

class ChatToItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_raw
    }
}
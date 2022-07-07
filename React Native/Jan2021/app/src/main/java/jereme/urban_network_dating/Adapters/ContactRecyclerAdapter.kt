package com.example.messengerapp
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jereme.urban_network_dating.List.UserModel
import jereme.urban_network_dating.R
import java.util.*

class ContactRecyclerAdapter(private var list: ArrayList<UserModel>, private var listener: UserClickListener)
  : RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder>() {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.item_chat_list, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

  override fun getItemCount(): Int = list.size

  fun showUserOnline(updatedUser: UserModel) {
    list.forEachIndexed { index, element ->
      if (updatedUser.id == element.id) {
        updatedUser.online = true
        list[index] = updatedUser
        notifyItemChanged(index)
      }

    }
  }

  fun showUserOffline(updatedUser: UserModel) {

    list.forEachIndexed { index, element ->
      if (updatedUser.id == element.id) {
        updatedUser.online = false
        list[index] = updatedUser
        notifyItemChanged(index)
      }
    }

  }

  fun add(user: UserModel) {

    list.add(user)
    notifyDataSetChanged()

  }
  fun search(userlist: ArrayList<UserModel>) {

    list = userlist
    notifyDataSetChanged()

  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//    private val nameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
//    private val presenceImageView: ImageView = itemView.findViewById(R.id.presenceImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.message)
    private val presenceImageView: ImageView = itemView.findViewById(R.id.image)
    private val onlineImageView: ImageView = itemView.findViewById(R.id.dot_online)
    fun bind(currentValue: UserModel) = with(itemView) {
      this.setOnClickListener {
        listener.onUserClicked(currentValue)
      }
    nameTextView.text = currentValue.name
      if (currentValue.online) {
        onlineImageView.visibility= View.VISIBLE;
        onlineImageView.setImageDrawable(ContextCompat.getDrawable(this.context,R.drawable.online_dot))
      } else {
        onlineImageView.visibility= View.GONE;

      }
      if (currentValue.photo == "" ||currentValue.photo== "null") {
        presenceImageView.setImageResource(R.drawable.default_head_icon)
      } else {
        val photourl = "https://urban.network/" + currentValue.photo
        Picasso.with(this.context).load(photourl).into(presenceImageView)
      }

    }

  }

  interface UserClickListener {
    fun onUserClicked(user: UserModel)
  }

}

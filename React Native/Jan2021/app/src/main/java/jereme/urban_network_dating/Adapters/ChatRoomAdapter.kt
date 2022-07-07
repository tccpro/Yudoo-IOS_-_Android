package jereme.urban_network_dating.Adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView
import jereme.urban_network_dating.List.MessageModel
import jereme.urban_network_dating.NewHomeActivity.profile_id
import jereme.urban_network_dating.R
import java.util.*

class ChatRoomAdapter(private var list: ArrayList<MessageModel>)
  : RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int)
      = holder.bind(list[position])

  override fun getItemCount(): Int = list.size
  fun <T> reverseList(list: List<T>): MutableList<T> {
    val reverse: MutableList<T> = ArrayList(list.size)
    list.indices.reversed().forEach { reverse.add(list[it]) }
    return reverse
  }
  fun add(message: MessageModel) {
    list.add(message)
   // list.reverse()
    notifyDataSetChanged()
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val messageTextView: EmojiconTextView = itemView.findViewById(R.id.text)
    private val messageTextlayout: LinearLayout = itemView.findViewById(R.id.message_item)
    private val videoLayout: VideoView = itemView.findViewById(R.id.video_view)
    private val messageImagelayout: LinearLayout = itemView.findViewById(R.id.imagelayout)
    private val messageImageView: ImageView = itemView.findViewById(R.id.imageview)
    private val avatarImageView: RoundedImageView = itemView.findViewById(R.id.avatar)
    private val cardView: CardView = itemView.findViewById(R.id.cardView)
    private val layoutTextView: LinearLayout = itemView.findViewById(R.id.message_item)
    fun bind(message: MessageModel): Unit = with(itemView) {
    val params = cardView.layoutParams as RelativeLayout.LayoutParams

      if(message.type.equals("text")) {

        messageTextlayout.visibility=View.VISIBLE
        messageImagelayout.visibility=View.GONE
        messageTextView.text = message.message
        avatarImageView.visibility=View.VISIBLE
        if(message.photo.equals("") && message.photo=="null"){
          avatarImageView.setImageResource(R.drawable.default_head_icon)
        } else {
          val photourl = "https://urban.network/" + message.photo
          Picasso.with(itemView.context).load(photourl).into(avatarImageView)
        }
        //if (message.senderId== Singleton.getInstance().currentUser.id) {
        if (message.senderId == profile_id) {
         avatarImageView.visibility=View.GONE
          params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          messageTextView.setTextColor(resources.getColor(R.color.whiteColor))
          layoutTextView.setBackgroundResource(R.drawable.user_message)
        }

      }else if(message.type.equals("image")){

        messageTextlayout.visibility=View.GONE
        messageImagelayout.visibility=View.VISIBLE
        messageImageView.visibility=View.VISIBLE
        videoLayout.visibility=View.GONE
        if ( message.message == "" ||  message.message == "null") {
          messageImageView.setImageResource(R.drawable.default_head_icon)
        } else {
          val photourl = "https://urban.network/pictures/" + message.message
          Picasso.with(itemView.context).load(photourl).into(messageImageView)
        }

        if (message.senderId == profile_id) {

          params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          messageImagelayout.setBackgroundResource(R.drawable.user_message)
        }

      } else if(message.type.equals("video")){

        messageTextlayout.visibility=View.GONE
        messageImageView.visibility=View.GONE
        messageImagelayout.visibility=View.VISIBLE
        videoLayout.visibility=View.VISIBLE
        if ( message.message == "" ||  message.message == "null") {
          messageImageView.setImageResource(R.drawable.default_head_icon)
        } else {

          val videourl = "https://urban.network/pictures/" + message.message
          videoLayout.setVideoPath(videourl)
         // videoLayout.setMediaController(MediaController(itemView.context))
          videoLayout.requestFocus()
          // mVideoView.start();
          // mVideoView.start();
          videoLayout.setKeepScreenOn(true);
         // videoLayout.stop
          videoLayout.requestFocus();
          // mVideoView.start();
//          videoLayout.setOnPreparedListener(MediaPlayer.OnPreparedListener {
//
//            videoLayout.start();
//          })
          videoLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
             if (videoLayout.isPlaying()) {
              videoLayout.pause()

              //
              videoLayout.getCurrentPosition()
               return@OnTouchListener false
            } else {


              videoLayout.start()
               return@OnTouchListener false
            }
          })
        }

          if (message.senderId == profile_id) {

            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            messageImagelayout.setBackgroundResource(R.drawable.user_message)
          }
        }

    }
  }
}
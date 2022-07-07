package jereme.urban_network_dating.Chats;

//import kotlinx.android.synthetic.main.activity_contact_list.*
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.ContactRecyclerAdapter
import com.example.messengerapp.RetrofitInstance
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.User
import com.pusher.client.util.HttpAuthorizer
import jereme.urban_network_dating.Chats.ChatTapFragment.listnerObj
import jereme.urban_network_dating.List.UserModel
import jereme.urban_network_dating.LoginActivity.nodebase_url
import jereme.urban_network_dating.NewHomeActivity.profile_id
import jereme.urban_network_dating.R
import jereme.urban_network_dating.Utils.toUserModel
import kotlinx.android.synthetic.main.memberlist.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity : Fragment(), ContactRecyclerAdapter.UserClickListener, ChatTapFragment.OnSearchViewText {
  private var mAdapter = ContactRecyclerAdapter(ArrayList(), this)
  private val arraListAll : ArrayList<UserModel> = ArrayList<UserModel>()
  var progress: ProgressDialog? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val v = inflater?.inflate(R.layout.memberlist, container, false)
    //  Inflate the layout for this fragment
    //  System.out.println(bundle.getString("photo"));
    listnerObj = this
    setupRecyclerView(v)
    fetchUsers()
    subscribeToChannel()
    return v;
  }

//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.fragment_chat)
//    setupRecyclerView()
//    fetchUsers()
//    subscribeToChannel()
//  }

  private fun setupRecyclerView(v:View) {
    with(v.recyclerView_chat) {
      layoutManager = LinearLayoutManager(activity)
      adapter = mAdapter
    }
  }

  private fun fetchUsers() {
    progress = ProgressDialog(activity)
    progress?.setTitle(resources.getString(R.string.loading))
    progress?.setMessage(resources.getString(R.string.members) + "...")
    progress?.setCancelable(false) // disable dismiss by tapping outside of the dialog
    progress?.show()

    RetrofitInstance.retrofituser.getUsers(profile_id).enqueue(object : Callback<List<UserModel>> {
      override fun onFailure(call: Call<List<UserModel>>?, t: Throwable?) {
        System.out.println(t?.localizedMessage);
        progress?.dismiss()
      }

      override fun onResponse(call: Call<List<UserModel>>?, response: Response<List<UserModel>>?) {
        progress?.dismiss()
        for (user in response!!.body()!!) {
          //  if (user.id != Singleton.getInstance().currentUser.id) {
            mAdapter.add(user)
            arraListAll.add(user)
          //}
        }
      }
    })
  }

  private fun subscribeToChannel() {
    //  val authorizer = HttpAuthorizer("http://192.168.0.105:3000/pusher/auth/presence")
    val authorizer = HttpAuthorizer(nodebase_url + "pusher/auth/presence")
    //  val authorizer = HttpAuthorizer(base_url +"chat/presence.php")
    val options = PusherOptions().setAuthorizer(authorizer)
    options.setCluster("mt1")

    val pusher = Pusher("4623ab31f34d74c8dc88", options)
    pusher.connect()

    pusher.subscribePresence("presence-channel",
      object : PresenceChannelEventListener {
        override fun onUsersInformationReceived(p0: String?, users: MutableSet<User>?) {
          for (user in users!!) {
            //Singleton.getInstance().currentUser.id
            if (user.id!= profile_id) {
              activity?.runOnUiThread {
                mAdapter.showUserOnline(user.toUserModel())
              }
            }
          }
        }

//        override fun onEvent(p0: String?, p1: String?, p2: String?) {
//
//        }

        override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
          //Log.e("Pusher", p1!!.message)
        }

        override fun onEvent(event: PusherEvent?) {
          TODO("Not yet implemented")
        }

        override fun onSubscriptionSucceeded(p0: String?) {
          Log.i("Pusher", "Subscription succeeded")
        }

        override fun userSubscribed(channelName: String, user: User) {
          activity?.runOnUiThread {
            mAdapter.showUserOnline(user.toUserModel())
          }
        }

        override fun userUnsubscribed(channelName: String, user: User) {
          activity?.runOnUiThread {
            mAdapter.showUserOffline(user.toUserModel())
          }
        }
      })
  }

  override fun onUserClicked(user: UserModel) {
    val intent = Intent(activity, ChatRoom::class.java)
    intent.putExtra(ChatRoom.EXTRA_ID, user.id)
    intent.putExtra(ChatRoom.EXTRA_NAME, user.name)
    //  intent.putExtra(ChatRoom.EXTRA_COUNT, user.count)
    intent.putExtra(ChatRoom.EXTRA_COUNT, Integer.parseInt(user.id))
    startActivity(intent)
  }

  fun searchContactText(searchtext: String?) {
    TODO("Not yet implemented")
    Toast.makeText(context,searchtext,Toast.LENGTH_LONG).show()
  }

  //@RequiresApi(Build.VERSION_CODES.N)
  override fun searchViewText(searchtext: String?) {
    //  Toast.makeText(context, searchtext, Toast.LENGTH_LONG).show()
    val arraList = arraListAll.filter{ contact->contact.name.toLowerCase().contains(searchtext!!.toLowerCase()) }
    mAdapter.search(arraList as java.util.ArrayList<UserModel>)
  }
}

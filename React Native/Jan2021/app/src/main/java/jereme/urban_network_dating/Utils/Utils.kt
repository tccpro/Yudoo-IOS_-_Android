package jereme.urban_network_dating.Utils;


import com.pusher.client.channel.User
import jereme.urban_network_dating.List.UserModel
import org.json.JSONObject

fun User.toUserModel(): UserModel {
  val jsonObject = JSONObject(this.info)
  val name = jsonObject.getString("name")
  val numb = jsonObject.getInt("count")
  val photo = jsonObject.getString("photo")
  return UserModel(this.id, name, numb, photo)
}
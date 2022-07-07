package jereme.urban_network_dating.API

import jereme.urban_network_dating.List.UserModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

  @POST("login")
  fun login(@Body body:RequestBody): Call<UserModel>

//  @POST("chat/chat.php")
//////  fun sendMessage(@Query("sender_id") sender_id:String,
//////                  @Query("channelname") channelname:String,
//////                  @Query("message") message:String): Call<String>

  @POST("send-message")
  fun sendMessage(@Body body:RequestBody): Call<String>

  @GET("member/searchmember.php")
  fun getUsers(@Query("id") id:String): Call<List<UserModel>>

}
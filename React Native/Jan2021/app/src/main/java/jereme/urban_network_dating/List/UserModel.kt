package jereme.urban_network_dating.List;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class UserModel(@SerializedName("id") @Expose var id: String,
                     @SerializedName("name") @Expose var name: String,
                     @SerializedName("count") @Expose var count: Int,
                     @SerializedName("photo") @Expose var photo: String,
                     var online:Boolean = false) {


}
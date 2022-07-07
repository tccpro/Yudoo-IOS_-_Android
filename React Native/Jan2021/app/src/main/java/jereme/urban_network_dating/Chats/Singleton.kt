package jereme.urban_network_dating.Chats;

import jereme.urban_network_dating.List.UserModel

class Singleton {
  companion object {
    private val ourInstance = Singleton()
    fun getInstance(): Singleton {
      return ourInstance
    }
  }
  lateinit var currentUser: UserModel
}
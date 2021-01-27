package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User

class UserInfoCache {
    private val cachedUsers = mutableMapOf<Long, User?>()

    fun getOrRetrieveUserInfo(jda: JDA, userId: Long) = cachedUsers.getOrPut(userId) {
        try {
            jda.retrieveUserById(userId)
                    .complete()
        } catch (e: Exception) { null }
    }
}
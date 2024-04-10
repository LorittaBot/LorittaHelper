package net.perfectdreams.loritta.morenitta.interactions.commands.options

import net.dv8tion.jda.api.entities.channel.Channel
import net.perfectdreams.i18nhelper.core.keydata.StringI18nData

open class ApplicationCommandOptions {
    companion object {
        val NO_OPTIONS = object : ApplicationCommandOptions() {}
    }

    val registeredOptions = mutableListOf<OptionReference<*>>()

    fun string(name: String, description: String, builder: StringDiscordOptionReference<String>.() -> (Unit) = {}) = StringDiscordOptionReference<String>(name, description, true)
        .apply(builder)
        .also { registeredOptions.add(it) }

    fun optionalString(name: String, description: String, builder: StringDiscordOptionReference<String?>.() -> (Unit) = {}) = StringDiscordOptionReference<String?>(name, description, false)
        .apply(builder)
        .also { registeredOptions.add(it) }

    fun boolean(name: String, description: String, builder: BooleanDiscordOptionReference<Boolean>.() -> (Unit) = {}) = BooleanDiscordOptionReference<Boolean>(name, description, true)
        .apply(builder)
        .also { registeredOptions.add(it) }

    fun optionalBoolean(name: String, description: String, builder: BooleanDiscordOptionReference<Boolean?>.() -> (Unit) = {}) = BooleanDiscordOptionReference<Boolean?>(name, description, false)
        .apply(builder)
        .also { registeredOptions.add(it) }

    fun long(name: String, description: String, requiredRange: LongRange? = null) = LongDiscordOptionReference<Long>(name, description, true, requiredRange)
        .also { registeredOptions.add(it) }

    fun optionalLong(name: String, description: String, requiredRange: LongRange? = null) = LongDiscordOptionReference<Long?>(name, description, false, requiredRange)
        .also { registeredOptions.add(it) }

    fun user(name: String, description: String) = UserDiscordOptionReference<UserAndMember>(name, description, true)
        .also { registeredOptions.add(it) }

    fun optionalUser(name: String, description: String) = UserDiscordOptionReference<UserAndMember?>(name, description, false)
        .also { registeredOptions.add(it) }

    fun channel(name: String, description: String) = ChannelDiscordOptionReference<Channel>(name, description, true)
        .also { registeredOptions.add(it) }

    fun optionalChannel(name: String, description: String) = ChannelDiscordOptionReference<Channel?>(name, description, false)
        .also { registeredOptions.add(it) }
}
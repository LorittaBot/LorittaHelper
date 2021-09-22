package net.perfectdreams.loritta.helper.utils.whydothisifyouaregoingtogetbannedanyway

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.utils.Constants
import java.time.Instant

class WhyDoThisIfYouAreGoingToGetBannedAnywayListener : ListenerAdapter() {
    companion object {
        val BLOCKED_AVATAR_HASHES = listOf(
            "066f4e66aba7193a686b71a0673a399f"
        )

        val BLOCKED_NAMES = listOf(
            Regex("l[e3][o0]x[i1]s", RegexOption.IGNORE_CASE),
            Regex("tr[a4]j[a4]d[o0]", RegexOption.IGNORE_CASE),
        )

        val BLOCKED_NAMES_MULTI_MATCH = listOf(
            BlockedNamesMultiMatch(
                listOf(
                    Regex("k[a4]n[e3]k[i1íe]", RegexOption.IGNORE_CASE),
                    Regex("tr[a4]j[a4]d[o0]", RegexOption.IGNORE_CASE)
                )
            ),
            BlockedNamesMultiMatch(
                listOf(
                    Regex("k[a4]n[e3]k[i1íe]", RegexOption.IGNORE_CASE),
                    Regex("l[e3][o0]x[i1]s", RegexOption.IGNORE_CASE)
                )
            ),
            BlockedNamesMultiMatch(
                listOf(
                    Regex("k[a4]n[e3]k[i1íe]", RegexOption.IGNORE_CASE),
                    Regex("rdg", RegexOption.IGNORE_CASE)
                )
            )
        )
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        // Okay, time to check this
        if (event.author.avatarId in BLOCKED_AVATAR_HASHES) {
            // Bye
            event.guild.ban(
                event.author,
                7,
                "[WDTIYAGTGBA™] Banned due to Avatar Hash!",
            ).queue()
            return
        }

        // Now check the names!
        val firstRegexMatch = BLOCKED_NAMES.firstOrNull {
            it.find(event.author.name) != null
        }
        if (firstRegexMatch != null) {
            // Also bye!
            event.guild.ban(
                event.author,
                7,
                "[WDTIYAGTGBA™] Banned due to name matching RegEx ${firstRegexMatch.pattern}",
            ).queue()
            return
        }

        // Now check the names (multimatch)
        val firstMultinameRegexMatch = BLOCKED_NAMES_MULTI_MATCH.firstOrNull {
            it.regexes.all {
                it.find(event.author.name) != null
            }
        }

        if (firstMultinameRegexMatch != null) {
            // Also bye! (again!)
            event.guild.ban(
                event.author,
                7,
                "[WDTIYAGTGBA™] Banned due to multi name matching RegExes ${firstMultinameRegexMatch.regexes.joinToString { it.pattern }}",
            ).queue()
            return
        }

        // And now check the message's content
        // Because people may talk about it, we will only punish if the account was recently created
        if (event.author.timeCreated >= Instant.now().atZone(Constants.TIME_ZONE_ID).minusDays(3L).toOffsetDateTime()) {
            val message = event.message.contentRaw

            // Now check the message!
            val firstMessageRegexMatch = BLOCKED_NAMES.firstOrNull {
                it.find(message) != null
            }
            if (firstMessageRegexMatch != null) {
                // Also bye!
                event.guild.ban(
                    event.author,
                    7,
                    "[WDTIYAGTGBA™] Banned due to message matching RegEx ${firstMessageRegexMatch.pattern}",
                ).queue()
                return
            }

            // Now check the message (multimatch)
            val firstMessageMultinameRegexMatch = BLOCKED_NAMES_MULTI_MATCH.firstOrNull {
                it.regexes.all {
                    it.find(message) != null
                }
            }
            if (firstMessageMultinameRegexMatch != null) {
                // Also bye! (again!)
                event.guild.ban(
                    event.author,
                    7,
                    "[WDTIYAGTGBA™] Banned due to multi message matching RegExes ${firstMessageMultinameRegexMatch.regexes.joinToString { it.pattern }}",
                ).queue()
                return
            }
        }

        // And that's it! :sparkles:
    }

    data class BlockedNamesMultiMatch(
        val regexes: List<Regex>
    )
}
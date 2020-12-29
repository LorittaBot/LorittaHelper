package net.perfectdreams.loritta.helper.listeners

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.io.File
import java.net.URL

class ApproveFanArtListener(val m: LorittaHelper, val config: FanArtsConfig) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    var fanArtOverrideSettings: FanArtOverrideSettings? = null

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        if (!event.message.contentRaw.startsWith("h!fan_arts_override"))
            return

        val member = event.member ?: return

        if (!member.roles.any { it.idLong == config.approveFanArtsRoleId })
            return

        val contentRaw = event.message.contentRaw
                .split("\n")

        val resetSettings = contentRaw.firstOrNull { it.startsWith("reset") }
        if (resetSettings != null) {
            fanArtOverrideSettings = null
            event.channel.sendMessage("Override resetado!").queue()
            return
        }

        val fileName = contentRaw.firstOrNull { it.startsWith("imageFileName:") }?.trim()?.removePrefix("imageFileName:")
        val tags = contentRaw.firstOrNull { it.startsWith("tags:") }?.removePrefix("tags:")?.split(",")?.map { it.trim() }
        val artistFileName = contentRaw.firstOrNull { it.startsWith("artistFileName:") }?.trim()?.removePrefix("artistFileName:")
        val artistFileNameOnImage = contentRaw.firstOrNull { it.startsWith("artistFileNameOnImage:") }?.trim()?.removePrefix("artistFileNameOnImage:")

        fanArtOverrideSettings = FanArtOverrideSettings(
                fileName,
                tags,
                artistFileName,
                artistFileNameOnImage
        )

        event.channel.sendMessage("Override criado! $fanArtOverrideSettings").queue()
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.channel.idLong !in config.channels
                || !event.reactionEmote.isEmote
                || event.reactionEmote.idLong != config.emoteId
                || !event.member.roles.any { it.idLong == config.approveFanArtsRoleId })
            return

        GlobalScope.launch(m.executor) {
            try {
                val message = event.channel.retrieveMessageById(event.messageIdLong).await()

                val attachment = message.attachments.firstOrNull() ?: return@launch

                val userId = message.author.idLong

                val userName = message.author.name

                var artistId: String? = null

                logger.info { "Tentando adicionar fan art de $userId ($userName) - URL: ${attachment.url} " }

                for (it in File(config.fanArtArtistsFolder).listFiles()) {
                    if (it.extension == "conf") {
                        val text = it.readText()
                        if (text.contains("        id = \"$userId\"")) {
                            logger.info { "Arquivo encontrado para $userId ($userName) - $it" }
                            artistId = it.nameWithoutExtension
                            break
                        }
                    }
                }

                artistId = artistId ?: fanArtOverrideSettings?.artistFileName ?: userName.replace(Regex("[^a-zA-Z0-9]"), "")
                        .toLowerCase()
                        .trim()
                        .replace(" ", "_")

                if (artistId.isBlank())
                    artistId = userId.toString()

                logger.info { "Artist ID para $userId ($userName) é $artistId" }

                val date = "${message.timeCreated.year}-${message.timeCreated.monthValue.toString().padStart(2, '0')}-${message.timeCreated.dayOfMonth.toString().padStart(2, '0')}"

                val fanArtUrl = attachment.url

                val ext = if (fanArtUrl.endsWith("jpg"))
                    "jpg"
                else if (fanArtUrl.endsWith("gif"))
                    "gif"
                else
                    "png"

                var artistNameOnFiles = fanArtOverrideSettings?.artistFileNameOnImage ?: userName.replace(Regex("[^a-zA-Z0-9]"), "")
                        .trim()
                        .replace(" ", "_")

                if (artistNameOnFiles.isBlank())
                    artistNameOnFiles = userId.toString()

                logger.info { "Nome do arquivo para $userId ($userName) é $artistNameOnFiles" }

                val fanArtName = run {
                    val first = File(config.fanArtFilesFolder, "${fanArtOverrideSettings?.imageFileName ?: "Loritta"}_-_$artistNameOnFiles.$ext")
                    if (!first.exists())
                        first.name
                    else {
                        var recursiveness = 2
                        var f: File
                        do {
                            f = File(config.fanArtFilesFolder, "${fanArtOverrideSettings?.imageFileName ?: "Loritta"}_${recursiveness}_-_$artistNameOnFiles.$ext")
                            recursiveness++
                        } while (f.exists())

                        f.name
                    }
                }

                logger.info { "ID do usuário (Discord): $userId" }
                logger.info { "Nome do usuário: $userName" }
                logger.info { "ID do artista: $artistId" }
                logger.info { "Data da Fan Art: ${date}" }
                logger.info { "URL da Fan Art: $fanArtUrl" }
                logger.info { "Nome da Fan Art: $fanArtName" }

                val contents = URL(fanArtUrl).openConnection().getInputStream().readAllBytes()
                val imageFile = File(config.fanArtFilesFolder, fanArtName).apply {
                    this.writeBytes(contents)
                }

                logger.info { "Fan Art de $userId ($userName) - URL: ${attachment.url} foi salva em $imageFile!" }

                val artistFile = File(config.fanArtArtistsFolder, "$artistId.conf")

                val fanArtSection = """    {
        |        file-name = "$fanArtName"
        |        created-at = "$date"
        |        tags = [${fanArtOverrideSettings?.tags?.joinToString(", ", transform = { "\"$it\"" }) ?: ""}]
        |    }
    """.trimMargin()

                val isNewArtist = !artistFile.exists()

                if (!isNewArtist) {
                    logger.info { "Arquivo do artista já existe! Vamos apenas inserir a fan art..." }
                    val artistTemplate = artistFile.readText()
                    val lines = artistTemplate.lines().toMutableList()

                    val insertAt = lines.indexOf("]")
                    lines.addAll(insertAt, fanArtSection.lines())

                    artistFile.writeText(lines.joinToString("\n"))
                } else {
                    logger.info { "Criando um arquivo de artista para a fan art..." }

                    val fullArtistTemplate = """id = "$artistId"

info {
    name = "$userName"
}

fan-arts = [
$fanArtSection
]

networks = [
    {
        type = "discord"
        id = "$userId"
    }
]
"""

                    artistFile.writeText(fullArtistTemplate)
                }

                val userMessage = StringBuilder()
                userMessage.append("A sua Fan Art (<https://loritta.website/assets/img/fanarts/$fanArtName>) foi adicionada no website! <a:lori_temmie:515330130495799307>")
                userMessage.append("\n\n")
                userMessage.append("Aonde será que eu irei colocar a sua fan art... Talvez eu irei colocar ${config.placesToPlaceStuff.random()}!")
                userMessage.append("\n\n")

                val fanArtArtistGuildMember = try { message.guild.retrieveMember(message.author).await() } catch (e: Exception) { null }

                if (event.channel.idLong == config.firstFanArtChannelId) {
                    userMessage.append("Obrigada por ser uma pessoa incrível!! Te amooo!! (como amiga, é clarooo!) <:lori_heart_1:728722208825802873><:lori_heart_2:728722238924128257>")
                    userMessage.append("\n\n")
                    userMessage.append("Agora você tem permissão para mandar mais fan arts para mim em <#583406099047252044>, mandar outros desenhos fofis em <#510601125221761054> e conversar com outros artistas em <#574387310129315850>! <:lori_owo:500751182625767425> (Observação: Demora um pouquinho até receber o cargo!)")

                    // Enviar a fan art no canal de fan arts da Lori
                    val channel = event.guild.getTextChannelById(config.fanArtsChannelId)

                    channel?.sendMessage(
                            "Fan Art de <@$userId> <:lori_heart:640158506049077280> https://loritta.website/assets/img/fanarts/$fanArtName"
                    )?.await()

                    val role = message.guild.getRoleById(config.firstFanArtRoleId)

                    if (role != null && fanArtArtistGuildMember != null && fanArtArtistGuildMember.roles.contains(role)) {
                        message.guild.removeRoleFromMember(fanArtArtistGuildMember, role)
                                .await()
                    }
                } else {
                    userMessage.append("Obrigada por ser uma pessoa incrível e por continuar a fazer fan arts de mim (tô até emocionada <:lori_sob:556524143281963008>)... Te amooo!! (como amiga, é clarooo!) <:lori_heart_1:728722208825802873><:lori_heart_2:728722238924128257>")
                }

                userMessage.append("\n\n")
                userMessage.append("Sério, obrigada pela fan art, continue assim e continue a transformar o mundo em um lugar melhor! <a:lori_pat:706263175892566097>")

                event.user.openPrivateChannel().await().sendMessage("A incrível Fan Art foi adicionada com sucesso! :3 https://loritta.website/assets/img/fanarts/$fanArtName").await()

                try {
                    fanArtArtistGuildMember?.user?.openPrivateChannel()?.await()?.sendMessage(userMessage.toString())
                            ?.await()
                } catch (e: Exception) {}
            } catch (e: Exception) {
                logger.error(e) { "Erro ao adicionar fan art" }
            }
        }
    }

    data class FanArtOverrideSettings(
            val imageFileName: String?,
            val tags: List<String>?,
            val artistFileName: String?,
            val artistFileNameOnImage: String?
    )
}
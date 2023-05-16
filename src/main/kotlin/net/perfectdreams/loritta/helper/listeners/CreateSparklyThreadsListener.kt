package net.perfectdreams.loritta.helper.listeners

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.perfectdreams.loritta.helper.utils.extensions.await

class CreateSparklyThreadsListener : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId == "create_sparkly_thread") {
            GlobalScope.launch {
                val txtChannel = event.guildChannel.asTextChannel()
                val thread = txtChannel.createThreadChannel("Denúncia criada por ${event.user.name}")
                    .await()
                // Delete parent message
                thread.retrieveParentMessage()
                    .await()
                    .delete()
                    .await()
                thread.addThreadMember(event.user).await()
                thread.sendMessage("<:pantufa_hi:997662575779139615> Olá ${event.user.asMention}, criei esse canal para a sua denúncia e já vou marcar a <@&332650495522897920> pra você!\n" +
                        "<:pantufa_smart:997671151587299348> Certifique-se de dizer tudo o que for necessário, incluindo o nickname do meliante e as provas!\n" +
                        "Espero que consiga a ajuda necessária <a:pantufa_pickaxe:997671670468853770>")
                    .await()

                event.reply("Thread para a sua denúncia foi criada com sucesso! Link do canal: ${thread.asMention}")
                    .setEphemeral(true)
                    .await()
            }
            return
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.contentRaw == "send_sparkly_thread_stuff" && event.author.idLong == 123170274651668480L) {
            GlobalScope.launch {
                event.channel.sendMessage(
                    MessageCreateBuilder()
                        .setEmbeds(
                            EmbedBuilder()
                                .setDescription("**Boas vindas ao canal de denúncias do SparklyPower!**\n<:pantufa_zap:1004450035154571325> Aqui você pode adquirir ajuda da Staff com algum meliante ou qualquer problema referente a quebras de regras, desde que relacionado ao SparklyPower.\n\n<:pantufa_shrug:1004449909816168489> **Antes de tudo,** certifique-se de que o seu problema não é algo a ser resolvido no <#994664055933517925>.\n<:pantufa_smart:997671151587299348> **E lembre-se,** o canal que será aberto a partir deste, é PÚBLICO! Se deseja fazer uma denúncia totalmente anônima, prossiga para o suporte do servidor.\n\n<:pantufa_clown:1004449971342426186> **NÃO** fique brincando de minimod (ficar respondendo as denúncias como se fosse um Staff para tentar ajudar), isso apenas atrapalha as resoluções e lota o canal de mensagens desnecessárias. Fazer isso, bem como ficar comentando nas denúncias, fará com que você seja punido, afinal isso é uma regra do servidor.")
                                .setImage("https://cdn.discordapp.com/attachments/363368805532958721/1107751402098405467/Banners_SuporteDenuncia.png")
                                .setAuthor(
                                    "Central de Denúncias",
                                    null,
                                    "https://cdn.discordapp.com/emojis/997671670468853770.gif?size=96&quality=lossless"
                                )
                                .setColor(39349)
                                .build()
                        )
                        .setActionRow(
                            Button.of(
                                ButtonStyle.PRIMARY,
                                "create_sparkly_thread",
                                "Abrir Thread",
                                Emoji.fromUnicode("➕")
                            )
                        )
                        .build()
                ).await()
            }
        }
    }
}
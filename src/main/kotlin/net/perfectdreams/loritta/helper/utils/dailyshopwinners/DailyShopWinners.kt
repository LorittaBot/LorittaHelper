package net.perfectdreams.loritta.helper.utils.dailyshopwinners

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.*

class DailyShopWinners(val m: LorittaHelper, val jda: JDA) {
    private val logger = KotlinLogging.logger {}

    fun start() {
        GlobalScope.launch {
            while (true) {
                val now = Instant.now()
                    .atOffset(ZoneOffset.UTC)

                val startOfNextMonth = Instant.now()
                    .atOffset(ZoneOffset.UTC)
                    .plusMonths(1)
                    .withMinute(0)
                    .withHour(0)
                    .withSecond(0)

                val diff = startOfNextMonth.toEpochSecond() - now.toEpochSecond()

                logger.info { "Broadcasting Daily Shop winners in ${diff} seconds!" }
                delay(diff * 1000)

                broadcastDailyShopWinners()
            }
        }
    }

    fun broadcastDailyShopWinners() {
        val lastMonthAtUtc = Instant.now()
            .atOffset(ZoneOffset.UTC)
            .minusMonths(1)

        val startOfPreviousMonth = LocalDateTime.of(
            lastMonthAtUtc.year,
            lastMonthAtUtc.monthValue,
            1,
            0,
            0
        ).atOffset(ZoneOffset.UTC)
            .toEpochSecond() * 1000

        // We can't just to Month.length because of leap years
        val yearMonthObject = YearMonth.of(lastMonthAtUtc.year, lastMonthAtUtc.monthValue)
        val daysInMonth = yearMonthObject.lengthOfMonth()

        val endOfPreviousMonth = LocalDateTime.of(
            lastMonthAtUtc.year,
            lastMonthAtUtc.monthValue,
            daysInMonth,
            23,
            59
        ).atOffset(ZoneOffset.UTC)
            .toEpochSecond() * 1000

        val topDailyShopSpenders = transaction(m.databases.lorittaDatabase) {
            val list = mutableListOf<Pair<Long, Long>>()

            TransactionManager.current().exec(
                """select "user", sum(money) from
    (
        select * from backgroundpayments where bought_at >= $startOfPreviousMonth and $endOfPreviousMonth >= bought_at
        UNION
        select * from profiledesignspayments where bought_at >= $startOfPreviousMonth and $endOfPreviousMonth >= bought_at
    )
as a group by "user" order by sum(money) desc LIMIT 5;"""
            ) {
                while (it.next()) {
                    val userId = it.getLong("user")
                    val sum = it.getLong("sum")

                    list.add(
                        Pair(
                            userId,
                            sum
                        )
                    )
                }
            }

            list
        }

        val channel = jda.getNewsChannelById(302976807135739916L)

        if (channel != null) {
            val winner1 = topDailyShopSpenders[0]
            val winner2 = topDailyShopSpenders[1]
            val winner3 = topDailyShopSpenders[2]

            val locale = Locale.forLanguageTag("pt")
            val lastMonthName = lastMonthAtUtc.month.getDisplayName(TextStyle.FULL, locale)

            val thisMonthName = Instant.now().atOffset(ZoneOffset.UTC).month.getDisplayName(TextStyle.FULL, locale)

            channel.sendMessage(
                """@everyone <@&334734175531696128> <:lori_yay_ping:640141673531441153>

<a:cat_groove:745273300850311228> **Todos os meses, no primeiro dia do mês, as top três pessoas que tiverem gasto mais sonhos na loja diária de itens no mês anterior irão ganhar Nitro Classic!** <a:cat_groove:745273300850311228>

**Parabéns para as top três pessoas deste mês!**
<:kawaii_one:542823112220344350> <@${winner1.first}>, que gastou *${winner1.second} sonhos* no mês de $lastMonthName
<a:kawaii_two:542823168465829907> <@${winner2.first}>, que gastou *${winner2.second} sonhos* no mês de $lastMonthName
<a:kawaii_three:542823194445348885> <@${winner3.first}>, que gastou *${winner3.second} sonhos* no mês de $lastMonthName

**Para o top 3:** Me mencione no chat de bate-papo para que eu te dê o Nitro Classic! Só não demore muito, se não eu irei dar o Nitro Classic para outra pessoa que ficou no topo! <:lori_feliz:519546310978830355>

Aproveite o novo mês para conseguir Nitro Classic! Vai se você acaba virando a pessoa que mais gastou sonhos no mês de $thisMonthName na loja diária? <:lori_smug:729723959284727808> <https://loritta.website/br/user/@me/dashboard/daily-shop>

*Psiu, precisando de sonhos? Então jogue no SparklyPower! O servidor de Minecraft (Survival) oficial da Loritta Morenitta, você pode transferir os sonhos da Loritta para lá e os sonhos do SparklyPower para a Loritta! `mc.sparklypower.net` <https://discord.gg/JYN6g2s>*"""
            ).queue()
        }
    }
}
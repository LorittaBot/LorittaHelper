package net.perfectdreams.loritta.helper.serverresponses

import net.perfectdreams.loritta.helper.serverresponses.english.AddEmotesOnMessageResponse
import net.perfectdreams.loritta.helper.serverresponses.english.AddLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.english.AnnouncementsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.BadgeResponse
import net.perfectdreams.loritta.helper.serverresponses.english.CanaryResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ChangePrefixResponse
import net.perfectdreams.loritta.helper.serverresponses.english.CommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ConfigureLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ConfigurePunishmentsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.DJLorittaResponse
import net.perfectdreams.loritta.helper.serverresponses.english.EmbedsArbitraryResponse
import net.perfectdreams.loritta.helper.serverresponses.english.EmbedsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.HelpMeResponse
import net.perfectdreams.loritta.helper.serverresponses.english.HowToSeeLorittasSourceCodeResponse
import net.perfectdreams.loritta.helper.serverresponses.english.HowToUseCommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.JoinLeaveResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LanguageResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LoriBrothersResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LoriMandarCmdsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LoriNameResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LoriOfflineResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LoriXpResponse
import net.perfectdreams.loritta.helper.serverresponses.english.LostAccountResponse
import net.perfectdreams.loritta.helper.serverresponses.english.MemberCounterResponse
import net.perfectdreams.loritta.helper.serverresponses.english.MentionChannelResponse
import net.perfectdreams.loritta.helper.serverresponses.english.MuteResponse
import net.perfectdreams.loritta.helper.serverresponses.english.NoStaffSpotResponse
import net.perfectdreams.loritta.helper.serverresponses.english.PantufaResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ProfileBackgroundResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ReceiveSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.english.SayResponse
import net.perfectdreams.loritta.helper.serverresponses.english.SendSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.english.SlowModeResponse
import net.perfectdreams.loritta.helper.serverresponses.english.SparklyPowerInfoResponse
import net.perfectdreams.loritta.helper.serverresponses.english.StarboardResponse
import net.perfectdreams.loritta.helper.serverresponses.english.SugestoesResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ThirdPartyBotsResponse
import net.perfectdreams.loritta.helper.serverresponses.english.TransferGarticosResponse
import net.perfectdreams.loritta.helper.serverresponses.english.ValorShipResponse
import net.perfectdreams.loritta.helper.serverresponses.english.VotarResponse
import net.perfectdreams.loritta.helper.serverresponses.english.WhoIsVieirinhaResponse

/**
 * Class holding a list containing all Loritta Helper's automatic responses (English)
 */
object EnglishResponses {
    val responses = listOf(
        AddEmotesOnMessageResponse(),
        AddLoriResponse(),
        AnnouncementsResponse(),
        BadgeResponse(),
        CanaryResponse(),
        ChangePrefixResponse(),
        CommandsResponse(),
        ConfigureLoriResponse(),
        ConfigurePunishmentsResponse(),
        DJLorittaResponse(),
        EmbedsArbitraryResponse(),
        EmbedsResponse(),
        HelpMeResponse(),
        HowToUseCommandsResponse(),
        JoinLeaveResponse(),
        LanguageResponse(),
        LoriBrothersResponse(),
        LoriMandarCmdsResponse(),
        LoriNameResponse(),
        LoriOfflineResponse(),
        LoriXpResponse(),
        LostAccountResponse(),
        MemberCounterResponse(),
        MentionChannelResponse(),
        MuteResponse(),
        PantufaResponse(),
        ProfileBackgroundResponse(),
        ReceiveSonhosResponse(),
        SayResponse(),
        SendSonhosResponse(),
        SlowModeResponse(),
        SparklyPowerInfoResponse(),
        StarboardResponse(),
        SugestoesResponse(),
        ThirdPartyBotsResponse(),
        TransferGarticosResponse(),
        ValorShipResponse(),
        VotarResponse(),
        WhoIsVieirinhaResponse(),
        NoStaffSpotResponse(),
        HowToSeeLorittasSourceCodeResponse()
    ).sortedByDescending { it.priority }
}
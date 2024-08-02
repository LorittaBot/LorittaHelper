package net.perfectdreams.loritta.helper.serverresponses.loritta

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.AddEmotesOnMessageResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.AddLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.AnnouncementsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.BadgeResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.CanaryResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ChangePrefixResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.CommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ConfigureLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ConfigurePunishmentsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.DJLorittaResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.EmbedsArbitraryResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.EmbedsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.HelpMeResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.HowToSeeLorittasSourceCodeResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.HowToUseCommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.JoinLeaveResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LanguageResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriBrothersResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriMandarCmdsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriNameResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriOfflineResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriXpResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LostAccountResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.MemberCounterResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.MentionChannelResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.MuteResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.NoStaffSpotResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.PantufaResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ProfileBackgroundResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ReceiveSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SayResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SendSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SlowModeResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SparklyPowerInfoResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.StarboardResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SugestoesResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ThirdPartyBotsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.TransferGarticosResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.ValorShipResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.VotarResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.WhoIsVieirinhaResponse
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig

/**
 * Class holding a list containing all Loritta Helper's automatic responses (English)
 */
class EnglishResponses(config: LorittaHelperConfig) {
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
        HelpMeResponse(config),
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
        ThirdPartyBotsResponse(config),
        TransferGarticosResponse(),
        ValorShipResponse(),
        VotarResponse(),
        WhoIsVieirinhaResponse(),
        NoStaffSpotResponse(),
        HowToSeeLorittasSourceCodeResponse()
    ).sortedByDescending { it.priority }
}
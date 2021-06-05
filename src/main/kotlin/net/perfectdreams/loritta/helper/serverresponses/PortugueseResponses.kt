package net.perfectdreams.loritta.helper.serverresponses

import net.perfectdreams.loritta.helper.serverresponses.portuguese.AboutMeResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.AddEmotesOnMessageResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.AddLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.AnnouncementsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.BadgeResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.CanaryResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ChangePrefixResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.CommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ConfigureLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ConfigurePunishmentsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.DJLorittaResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.EmbedsArbitraryResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.EmbedsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.HelpMeResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.HowDoIReportResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.HowToSeeLorittasSourceCodeResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.HowToUseCommandsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.JoinLeaveResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LanguageResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriBrothersResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriMandarCmdsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriNameResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriOfflineResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriXpResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.LostAccountResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.MemberCounterResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.MentionChannelResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.MuteResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.NoStaffSpotResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.PantufaResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ProfileBackgroundResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ReceiveSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ReportBugsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.SayResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.SendSonhosResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.SlowModeResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.SparklyPowerInfoResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.StarboardResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.SugestoesResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ThirdPartyBotsResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.TransferGarticosResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.UserNotShowingUpRankResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.ValorShipResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.VotarResponse
import net.perfectdreams.loritta.helper.serverresponses.portuguese.WhoIsVieirinhaResponse

/**
 * Class holding a list containing all Loritta Helper's automatic responses (Portuguese)
 */
object PortugueseResponses {
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
        HowToSeeLorittasSourceCodeResponse(),
        AboutMeResponse(),
        HowDoIReportResponse(),
        ReportBugsResponse(),
        UserNotShowingUpRankResponse()
    ).sortedByDescending { it.priority }
}

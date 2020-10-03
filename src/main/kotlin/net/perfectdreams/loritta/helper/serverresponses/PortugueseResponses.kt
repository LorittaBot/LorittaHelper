package net.perfectdreams.loritta.helper.serverresponses

import net.perfectdreams.loritta.helper.serverresponses.portuguese.*

/**
 * Class holding a list containing all Loritta Helper's automatic responses (Portuguese)
 */
object PortugueseResponses {
    val responses = listOf(
        AddEmotesOnMessageResponse(),
        AddLoriResponse(),
        BackgroundResponse(),
        BadgeResponse(),
        CanaryResponse(),
        ChangePrefixResponse(),
        CommandsResponse(),
        ConfigureLoriResponse(),
        DJLorittaResponse(),
        EmbedsArbitraryResponse(),
        EmbedsResponse(),
        HelpMeResponse(),
        JoinLeaveResponse(),
        LanguageResponse(),
        LoriBrothersResponse(),
        LoriMandarCmdsResponse(),
        LoriOfflineResponse(),
        LoriXpResponse(),
        LostAccountResponse(),
        MemberCounterResponse(),
        MentionChannelResponse(),
        MuteResponse(),
        PantufaResponse(),
        ProfileBackgroundResponse(),
        ReceiveSonhosResponse(),
        SendSonhosResponse(),
        SlowModeResponse(),
        StarboardResponse(),
        SugestoesResponse(),
        ValorShipResponse(),
        VotarResponse(),
        WhoIsVieirinhaResponse(),
        NoStaffSpotResponse()
    ).sortedByDescending { it.priority }
}
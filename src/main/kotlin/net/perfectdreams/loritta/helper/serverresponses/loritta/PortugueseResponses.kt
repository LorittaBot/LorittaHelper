package net.perfectdreams.loritta.helper.serverresponses.loritta

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.*
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig

/**
 * Class holding a list containing all Loritta Helper's automatic responses (Portuguese)
 */
class PortugueseResponses(config: LorittaHelperConfig) {
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
        HowToSeeLorittasSourceCodeResponse(),
        AboutMeResponse(),
        HowDoIReportResponse(),
        ReportBugsResponse(),
        UserNotShowingUpRankResponse(),
        TwoFactorAuthenticationRequirementResponse(),
        BomDiaECiaResponse(),
        DailyCaptchaDoesNotWorkResponse(),
        LorittaPremiumResponse(),
        CanIExchangeSonhosForSomethingElseResponse(),
        ReputationsResponse(),
        SocialNotificatorResponse(),
        LoriSendEmbedResponse()
    ).sortedByDescending { it.priority }
}

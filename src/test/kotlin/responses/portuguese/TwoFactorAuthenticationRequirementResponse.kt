package responses.portuguese

class TwoFactorAuthenticationRequirementResponse : PortugueseResponseTestBase(
    listOf(
        "Para pegar o prêmio, você precisa ativar autenticação em duas etapas na sua conta no Discord. Para ativar, vá nas configurações da sua conta no Discord! (Recomendamos utilizar o Authy para autenticação, caso o Discord esteja rejeitando o código de autenticação, verifique se o horário do seu celular está correto",
        "eu tento pegar o daily e a lori pede para eu usar o authy",
        "o daily tá dando problema de 2fa",
        "o daily não deixa eu pegar pois precisa de autenticação em duas etapas",
        "não consigo pegar o daily porque pede para eu usar o authy"
    )
)
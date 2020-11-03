<h1 align="center">🙋‍♀️ Loritta Helper 🙋‍♀️</h1>
<img height="250" src="https://media.discordapp.net/attachments/297732013006389252/773299252553711667/LoriChibi_4.png" align="right">

The bot that automatically answers **ALMOST** all questions about Loritta on its support server. 

## 📅 Info

* This bot is private, it can't be added to your server, but you can [self host](https://en.wikipedia.org/wiki/Self-hosting_(web_services)) it.
* We won't consider pull requests that are not useful.
* If you want help with contributing, you can call someone with the "Beeps and Boops" role in Loritta's community server, they know very well how to help you out.

# 🌎 Contributing

You can add new responses to Loritta Helper following these steps:

* First of all, you need to create a new class with a name that can describe what your response is, something like `ProfileBackgroundResponse.kt`, at the package: `net/perfectdreams/loritta/helper/serverresponses/{language}/Name.kt`

* Let's suppose you're going to create a response that will answer people about how to earn sonhos. The name should be something like `HowToGetSonhosResponse` or something related.

* Loritta detects the question using [RegEx](https://en.wikipedia.org/wiki/Regular_expression), there are some materials that we recommend:

    * [Tests](https://regexr.com/)
    * [Guide](https://medium.com/@alexandreservian/regex-um-guia-pratico-para-express%C3%B5es-regulares-1ac5fa4dd39f)

* Now, you need to make your class extends `RegExResponse`, so it can be considered by Lori.

* You should add the RegEx patterns at the class `init` block, something like this:

```kotlin
init {
    patterns.add("como")
    patterns.add("ganha|obtem|receber")
    patterns.add("sonhos|dreams|moedas|dinheiro".toPattern(Pattern.CASE_INSENSITIVE))
}
```

* After that, you want to specify the answer, so you need to add a method called `getResponse()`, it should look something like that:

```kotlin
override fun getResponse(): List<LorittaReply> = listOf(
    LorittaReply(Emotes.LORI_OWO, "You can earn sonhos using `+daily`."),
    LorittaReply(Emotes.LORI_PAT, "Or betting with users using `+coinflip bet`")
)
```

And then you can search for the class `net/perfectdreams/loritta/helper/serverresponses/{language}Responses.kt`, you can add your response there to register it.
If you have some kind of experience, please consider doing [unit tests](https://github.com/LorittaBot/LorittaHelper/tree/main/src/test/kotlin/responses/portuguese).

## 📊 License

This repository is under the [AGPL-3.0](https://github.com/LorittaBot/LorittaHelper/blob/main/LICENSE) license.

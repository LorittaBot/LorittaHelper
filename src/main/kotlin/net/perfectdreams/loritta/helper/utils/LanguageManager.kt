package net.perfectdreams.loritta.helper.utils

import com.ibm.icu.text.MessagePattern
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.i18nhelper.core.Language
import net.perfectdreams.i18nhelper.core.LanguageInfo
import net.perfectdreams.i18nhelper.core.TextBundle
import net.perfectdreams.i18nhelper.formatters.ICUFormatter
import org.yaml.snakeyaml.Yaml
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.reflect.KClass
import kotlin.streams.toList

/**
 * Manages translations
 */
class LanguageManager(
    val clazz: KClass<*>,
    val defaultLanguageId: String,
    languagesPath: String
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        private val snakeYaml = Yaml()
        private val serializationYaml = com.charleskorn.kaml.Yaml()
    }

    val languagesPath = "/${languagesPath.removePrefix("/").removeSuffix("/")}/"
    var languageContexts = mapOf<String, I18nContext>()
    var languages = mapOf<String, Language>()

    /**
     * Gets the Language by its ID, if the language doesn't exist, the default language ([defaultLanguageId]) will be used
     *
     * @param languageId the ID of the language
     * @return           the language, if the language doesn't exist, the default language will be loaded
     * @see              Language
     */
    fun getLanguageById(languageId: String): Language {
        return getLanguageOrNullById(languageId) ?: languages[defaultLanguageId]!!
    }

    /**
     * Gets the Language by its ID
     *
     * @param languageId the ID of the language
     * @return           the language
     * @see              Language
     */
    fun getLanguageOrNullById(languageId: String): Language? {
        return languages[languageId]
    }

    /**
     * Gets the I18nContext by its ID, if the context doesn't exist, the default context ([defaultLanguageId]) will be used
     *
     * @param languageId the ID of the language
     * @return           the context, if the language doesn't exist, the context of the default language will be loaded
     * @see              I18nContext
     */
    fun getI18nContextById(languageId: String): I18nContext {
        return getI18nContextOrNullById(languageId) ?: languageContexts[defaultLanguageId]!!
    }

    /**
     * Gets the I18nContext by its ID
     *
     * @param languageId the ID of the language
     * @return           the context
     * @see              I18nContext
     */
    fun getI18nContextOrNullById(languageId: String): I18nContext? {
        return languageContexts[languageId]
    }

    fun loadLanguage(id: String, loadedLanguages: Map<String, Language>): Language? {
        val path = getPathFromResources("$languagesPath$id/language.yml")
        require(path?.exists() == true) { "Missing \"language.yml\" for language $id!" }

        val languageInfoText = Files.readString(getPathFromResources("$languagesPath$id/language.yml"))
        val languageInfo = serializationYaml.decodeFromString<LanguageInfo>(languageInfoText)

        val inheritsFrom = languageInfo.inheritsFrom
        val inheritsFromLanguage = if (inheritsFrom != null) {
            loadedLanguages[inheritsFrom] ?: return null // Requires language inheritance, load later
        } else {
            null
        }

        return loadLanguage(id, languageInfo, inheritsFromLanguage)
    }

    /**
     * Initializes the [id] locale and adds missing translation strings to non-default languages
     *
     * @see Language
     */
    fun loadLanguage(id: String, languageInfo: LanguageInfo, inheritsFromLanguage: Language?): Language {
        val strings = (inheritsFromLanguage?.textBundle?.strings?.toMutableMap() ?: mutableMapOf())
        val lists = (inheritsFromLanguage?.textBundle?.lists?.toMutableMap() ?: mutableMapOf())
        val textBundle = TextBundle(
            strings,
            lists
        )

        loadLanguage(id, getPathFromResources("$languagesPath$id/")!!, strings, lists)

        // Before we say "okay everything is OK! Let's go!!" we are going to format every single string on the locale
        // to check if everything is really OK
        for ((key, string) in textBundle.strings) {
            try {
                MessagePattern(string)
            } catch (e: IllegalArgumentException) {
                logger.error { "String \"$string\" stored in \"$key\" from $id can't be formatted! If you are using {...} formatted placeholders, do not forget to add \\' before and after the placeholder!" }
                throw e
            }
        }

        return Language(languageInfo, textBundle)
    }

    /**
     * Initializes the [id] locale and adds missing translation strings to non-default languages
     *
     * @see Language
     */
    private fun loadLanguage(id: String, path: Path, strings: MutableMap<String, String>, lists: MutableMap<String, List<String>>) {
        val filesToBeParsed = Files.list(path)
            .filter { it.name != "language" }

        filesToBeParsed.forEach {
            if (it.isDirectory())
                loadLanguage(id, it, strings, lists)
            else {
                val yaml = snakeYaml.load<Map<String, Any>>(Files.readString(it))
                    .toMutableMap()

                // Does exactly what the variable says: Only matches single quotes (') that do not have a slash (\) preceding it
                // Example: It's me, Mario!
                // But if there is a slash preceding it...
                // Example: \'{@user}\'
                // It won't match!
                val singleQuotesWithoutSlashPrecedingItRegex = Regex("(?<!(?:\\\\))'")

                fun transformIntoFlatMap(map: MutableMap<String, Any>, prefix: String) {
                    map.forEach { (key, value) ->
                        if (value is Map<*, *>) {
                            transformIntoFlatMap(value as MutableMap<String, Any>, "$prefix$key.")
                        } else {
                            if (value is List<*>) {
                                lists[prefix + key] = try {
                                    (value as List<String>).map {
                                        it.replace(
                                            singleQuotesWithoutSlashPrecedingItRegex,
                                            "''"
                                        ) // Escape single quotes
                                            .replace("\\'", "'") // Replace \' with '
                                    }
                                } catch (e: ClassCastException) {
                                    // A LinkedHashMap does match the "is List<*>" check, but it fails when we cast the subtype to String
                                    // If that happens, we will just ignore the exception and use the raw "value" list.
                                    (value as List<String>)
                                }
                            } else if (value is String) {
                                strings[prefix + key] =
                                    value.replace(
                                        singleQuotesWithoutSlashPrecedingItRegex,
                                        "''"
                                    ) // Escape single quotes
                                        .replace("\\'", "'") // Replace \' with '
                            } else if (value == null) {
                                // Let's pretend this never happened
                            } else throw IllegalArgumentException("Invalid object type detected in YAML! $value")
                        }
                    }
                }

                if (it.parent.name == "commands") {
                    transformIntoFlatMap(yaml, "commands.command.${it.nameWithoutExtension}.")
                } else {
                    transformIntoFlatMap(yaml, "")
                }
            }
        }
    }

    /**
     * Initializes the available languages and adds missing translation strings to non-default languages
     *
     * @see BaseLocale
     */
    fun loadLanguages() {
        val notLoadedYetLanguages = Files.list(
            getPathFromResources(languagesPath) ?: error("Language folder $languagesPath is not present in the application resources folder!")
        ).filter {
            it.isDirectory() && !it.name.startsWith(".") // Ignore ".github"
        }
            .toList()
            .toMutableList()

        val languages = mutableMapOf<String, Language>()

        while (notLoadedYetLanguages.isNotEmpty()) {
            for (translation in notLoadedYetLanguages.toList()) { // We create a copy to avoid a ConcurrentModificationException
                val language = loadLanguage(translation.name, languages)
                if (language == null) {
                    logger.info { "Language ${translation.name} has inheritance in a not loaded yet language, loading later..." }
                } else {
                    logger.info { "Loaded ${translation.name} (${language.info.name})" }
                    languages[translation.name] = language
                    notLoadedYetLanguages.remove(translation)
                }
            }
        }

        logger.info { "Loaded ${languages.size} languages" }

        this.languages = languages
    }

    fun loadI18nContexts() {
        val i18nContexts = mutableMapOf<String, I18nContext>()

        for ((id, language) in languages) {
            i18nContexts[id] = I18nContext(
                ICUFormatter(Locale.forLanguageTag(language.info.formattingLanguageId)),
                language
            )
        }

        this.languageContexts = i18nContexts
    }

    fun loadLanguagesAndContexts() {
        loadLanguages()
        loadI18nContexts()
    }

    private fun getPathFromResources(path: String): Path? {
        // https://stackoverflow.com/a/67839914/7271796
        val resource = clazz.java.getResource(path) ?: return null
        val uri = resource.toURI()
        val dirPath = try {
            Paths.get(uri)
        } catch (e: FileSystemNotFoundException) {
            // If this is thrown, then it means that we are running the JAR directly (example: not from an IDE)
            val env = mutableMapOf<String, String>()
            FileSystems.newFileSystem(uri, env).getPath(path)
        }
        return dirPath
    }
}
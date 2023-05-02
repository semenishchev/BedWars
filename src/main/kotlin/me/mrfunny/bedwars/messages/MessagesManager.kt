package me.mrfunny.bedwars.messages

import me.mrfunny.api.CustomConfiguration
import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.players.PlayerData
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.IOUtil
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files

class MessagesManager(val gameManager: GameManager){

    private val messagesToLang = hashMapOf<String, HashMap<String, String>>()

    init {
        val languageFiles = File(gameManager.plugin.dataFolder, "lang")
        val languages = languageFiles.listFiles() ?: emptyArray()

        if(languages.isEmpty()) {
            val stream = MessagesManager::class.java.classLoader.getResourceAsStream("lang/en_us.yml")
            Files.write(
                File(languageFiles, "en_us.yml").toPath(),
                if(stream == null) byteArrayOf() else IOUtil.toByteArray(stream)
            )
        }

        for (listFile in languages) {
            val name = listFile.name
            if(!name.endsWith(".yml")) continue
            val configWrapper = CustomConfiguration(listFile)
            val config = configWrapper.get()
            val translations = hashMapOf<String, String>()
            for(key in config.getKeys(false)) {
                translations[key] = config.getString(key) ?: key
            }
            messagesToLang[name.replace(".yml", "")] = translations
        }
    }

    fun broadcast(message: String, vararg variables: Any?) {
        for (playerData in PlayerData.all()) {
            sendMessage(playerData, message, variables)
        }
    }

    fun sendMessage(data: PlayerData, value: String, vararg variables: Any?) {
        Bukkit.broadcastMessage(localise(data, value, variables))
    }

    fun localise(data: PlayerData, value: String, vararg variables: Any?) =
        String.format((messagesToLang[data.locale] ?: emptyMap())[value] ?: value, variables).colorize()

    companion object {
        fun message(value: String, gameManager: GameManager, player: Player): String {
            TODO("Get rid of it")
        }
    }

}
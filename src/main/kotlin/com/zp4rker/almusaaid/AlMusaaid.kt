package com.zp4rker.almusaaid

import com.zp4rker.almusaaid.trello.DataServer
import com.zp4rker.disbot.API
import com.zp4rker.disbot.BOT
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.bot
import com.zp4rker.disbot.extenstions.event.on
import com.zp4rker.disbot.extenstions.separator
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

fun main(args: Array<String>) {
    val trelloKey = args[1]
    val trelloToken = args[2]
    val channelId = args[3].toLong()

    val dataServer = DataServer(trelloKey, trelloToken, channelId)

    bot {
        name = "Al-Musā'id"
        version = Bot::class.java.`package`.implementationVersion

        token = args[0]
        prefix = "/"

        activity = Activity.listening("your commands...")

        intents = GatewayIntent.ALL_INTENTS

        quit = {
            dataServer.kill()
        }
    }

    API.on<ReadyEvent> {
        BOT.logger.separator()
        BOT.logger.info("Ready to serve!")
        BOT.logger.separator()
    }

    dataServer.start()

    API.on<GuildMessageReceivedEvent>({ it.message.contentRaw == "stop server" }) {
        dataServer.running = false
        dataServer.kill()
    }

    /*val predicate: (GuildMessageReceivedEvent) -> Boolean = {
        it.message.embeds.isNotEmpty() && it.message.embeds[0].description == "empty data"
                && arrayOf("Set due date", "Moved card").contains(it.message.embeds[0].title)
    }

    API.on(predicate) {
        val cardId = it.message.embeds[0].footer!!.text
        it.message.delete().queue()

        val cardData = JSONObject(request("GET", "https://api.trello.com/1/cards/$cardId", mapOf(
            "key" to trelloKey,
            "token" to trelloToken,
            "fields" to "due,name,idList"
        )))

        var embed: MessageEmbed = embed()

        if (it.message.embeds[0].title == "Set due date") {
            embed = embed {
                author { name = cardData.getString("name") }
                title { text = "Set due date" }
                colour = 0x000D63B2
                timestamp = OffsetDateTime.parse(cardData.getString("due"))
                footer { text = "Due by" }
            }
        } else if (it.message.embeds[0].title == "Moved card") {
            val listName = JSONObject(request("GET", "https://api.trello.com/1/lists/${cardData.getString("idList")}", mapOf(
                "key" to trelloKey,
                "token" to trelloToken,
                "fields" to "name"
            ))).getString("name")

            if (listName != "In Progress") return@on

            embed = embed {
                author { name = cardData.getString("name") }
                title { text = "Moved task to $listName" }
                colour = 0x000D63B2
                timestamp = it.message.embeds[0].timestamp
            }
        }

        val embedString = """{ "embeds": [${embed.toData()}] }"""
        request("POST", trelloWebhook, headers = mapOf("Content-Type" to "application/json"), content = embedString)
    }*/
}
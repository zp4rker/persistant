package com.zp4rker.almusaaid.command.trello

import com.zp4rker.almusaaid.Trello
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.embed
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.time.OffsetDateTime

/**
 * @author zp4rker
 */
object TCardCommand : Command(aliases = arrayOf("tcard", "tcardinfo", "trellocard"), minArgs = 1, permission = Permission.ADMINISTRATOR) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val id = args[0]
        val data = Trello.getCard(id)

        val embed = embed {
            title {
                text = data.getString("name")
                url = data.getString("shortLink")
            }

            field {
                title = "Description"
                text = data.getString("desc")
                inline = false
            }

            field {
                title = "List ID"
                text = data.getString("idList")
            }

            field {
                title = "Board ID"
                text = data.getString("idBoard")
            }
        }

        channel.sendMessage(embed).queue()
    }
}
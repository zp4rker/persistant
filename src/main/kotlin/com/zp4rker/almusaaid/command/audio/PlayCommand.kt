package com.zp4rker.almusaaid.command.audio

import com.zp4rker.almusaaid.PLAYER
import com.zp4rker.almusaaid.PMANAGER
import com.zp4rker.almusaaid.TSCHEDULER
import com.zp4rker.almusaaid.audio.TrackLoader
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object PlayCommand : Command(aliases = arrayOf("play", "p"), minArgs = 1) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        message.suppressEmbeds(true).queue()

        if (TSCHEDULER.getQueue().any { it.track.info.uri == args[0] }) {
            channel.sendMessage(embed {
                title { text = "Track already in queue!" }

                description = "```${args[0]}```"

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                color = "#ec644b"
            }).queue()
            return
        }

        PMANAGER.loadItemOrdered(PLAYER, args[0], TrackLoader(channel, message.author))
    }

}
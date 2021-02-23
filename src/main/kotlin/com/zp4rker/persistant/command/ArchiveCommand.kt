package com.zp4rker.persistant.command

import com.zp4rker.discore.API
import com.zp4rker.discore.command.Command
import com.zp4rker.discore.extenstions.awaitMessages
import com.zp4rker.discore.extenstions.event.expect
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object ArchiveCommand : Command(aliases = arrayOf("archive"), permission = Permission.ADMINISTRATOR, autoDelete = true) {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val m = channel.sendMessage("Are you sure you'd like to archive this channel? React with ✅ to confirm.").complete().apply { addReaction("✅").queue() }
        channel.expect<GuildMessageReactionAddEvent>({ it.messageId == m.id && it.user == message.author && it.reactionEmote.name == "✅" }, timeoutUnit = TimeUnit.MINUTES, timeout = 2, timeoutAction = { m.delete().queue() }) {
            API.getUserByTag(config.owner)!!.openPrivateChannel().complete().run {
                sendMessage("Which channel would you like to send the archive to?").complete()
                val c = this.awaitMessages({ m -> m.mentionedChannels.isNotEmpty() }).first().mentionedChannels[0]
                val msgs = gatherMessages(channel)
                val tf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                val output = msgs.joinToString("\n") { m ->
                    "[${m.timeCreated.atZoneSameInstant(ZoneId.systemDefault()).format(tf)}]" +
                            "${m.author.asTag}: ${m.contentRaw}"
                }
                val fileName = "${c.name}-${OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)}.txt"
                c.sendFile(output.toByteArray(), fileName).queue()
                channel.delete().queue()
            }
        }
    }

    private fun gatherMessages(channel: TextChannel): List<Message> {
        return run {
            val history = channel.history
            val total = mutableListOf<Message>()
            var msgs = history.retrievePast(100).complete().also(total::addAll)
            while (msgs.size == 100) msgs = history.retrievePast(100).complete().also(total::addAll)
            total
        }.filter { it.embeds.size < 1 }.sortedBy { it.timeCreated }
    }
}
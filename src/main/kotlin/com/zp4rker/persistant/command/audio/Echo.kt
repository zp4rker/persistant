package com.zp4rker.persistant.command.audio

import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.command.Command
import com.zp4rker.persistant.audio.EchoHandler
import com.zp4rker.persistant.config
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object Echo : Command() {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        if (message.author.asTag != config.owner) return

        val arg = args.getOrElse(0) { "" }.lowercase()
        LOGGER.debug("arg is $arg")
        if (arg == "start") {
            val guild = message.guild
            val am = guild.audioManager
            EchoHandler().let {
                am.receivingHandler = it
                am.sendingHandler = it
            }
            if (am.connectionStatus.name.contains("CONNECTING") || am.connectionStatus == ConnectionStatus.CONNECTED) return
            am.openAudioConnection(guild.voiceChannels.first())
            channel.sendMessage("Echo started!").queue()
        } else if (arg == "stop") {
            val am = message.guild.audioManager
            if (am.connectionStatus == ConnectionStatus.NOT_CONNECTED) return
            am.closeAudioConnection()
            channel.sendMessage("Echo stopped!").queue()
        }
    }
}
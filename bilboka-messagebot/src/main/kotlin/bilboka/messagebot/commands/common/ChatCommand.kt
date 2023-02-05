package bilboka.messagebot.commands.common

import bilboka.messagebot.Conversation

internal abstract class ChatCommand {
    abstract fun isMatch(message: String): Boolean
    abstract fun validUser(regTypeID: String, senderID: String): Boolean
    abstract fun execute(conversation: Conversation, message: String)
}

abstract class ChatState

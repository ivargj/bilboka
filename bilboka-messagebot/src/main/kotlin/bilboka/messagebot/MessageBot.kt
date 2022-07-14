package bilboka.messagebot

import bilboka.messagebot.commands.FuelRecordAdder
import bilboka.messagebot.commands.Helper
import bilboka.messagebot.commands.SmallTalk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var botMessenger: BotMessenger

    @Autowired
    lateinit var carBookExecutor: CarBookExecutor

    private val commandRegistry by lazy {
        setOf(
            FuelRecordAdder(carBookExecutor),
            SmallTalk(),
            Helper()
        )
    }

    fun processMessage(message: String, senderID: String) {
        logger.info("Mottok melding $message")

        commandRegistry.forEach {
            if (it.isMatch(message)) {
                botMessenger.sendMessage(it.execute(message), senderID)
                return
            }
        }

        botMessenger.sendMessage(
            "Forstod ikke helt hva du mente. Prøv igjen eller skriv 'hjelp' om du trenger informasjon.",
            senderID
        )
    }
}

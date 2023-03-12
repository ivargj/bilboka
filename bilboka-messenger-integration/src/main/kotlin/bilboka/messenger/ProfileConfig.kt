package bilboka.messenger

import bilboka.messenger.consumer.MessengerProfileAPIConsumer
import bilboka.messenger.dto.GetStarted
import bilboka.messenger.dto.MessengerProfileRequest
import bilboka.messenger.dto.PersistentMenu
import bilboka.messenger.dto.PersistentMenuItem
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ProfileConfig(
    var profileAPIConsumer: MessengerProfileAPIConsumer
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private var hasDone = false

    @Scheduled(fixedDelay = 1000, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    fun setProfileConfig() {
        if (!hasDone) {
            logger.info("Setting profile config")
            profileAPIConsumer.doProfileUpdate(
                MessengerProfileRequest(
                    persistentMenu = listOf(
                        PersistentMenu(
                            callToActions = listOf(
                                PersistentMenuItem(
                                    title = "Hjelp",
                                    payload = "hlp"
                                )
                            )
                        )
                    ),
                    getStarted = GetStarted("hlp")
                )
            )
            hasDone = true
            profileAPIConsumer.getCurrentProfileSettings(
                listOf(
                    "greeting",
                    "get_started",
                    "ice_breakers",
                    "persistent_menu"
                )
            )?.run {
                logger.info("Current profile config: {}", this.toString())
            }
        } else {
            logger.info("Profile config already set")
        }
    }
}

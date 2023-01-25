package bilboka.messenger.resource

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.MessengerWebhookRequest
import bilboka.messenger.service.FacebookMessageHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.codec.binary.Hex
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object MessengerWebhookConfig {
    const val SUBSCRIBE_MODE = "subscribe"
    const val PAGE_SUBSCRIPTION = "page"
    const val EVENT_RECEIVED_RESPONSE = "EVENT_RECEIVED"
}

@RestController
@RequestMapping("webhook")
class MessengerWebhookResource(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var facebookMessageHandler: FacebookMessageHandler

    @GetMapping
    fun get(
        @RequestParam(name = "hub.verify_token") token: String,
        @RequestParam(name = "hub.challenge") challenge: String,
        @RequestParam(name = "hub.mode") mode: String
    ): ResponseEntity<String> {
        if (messengerProperties.verifyToken == token && MessengerWebhookConfig.SUBSCRIBE_MODE == mode) {
            logger.info("Webhook verified!")
            return ResponseEntity.ok(challenge)
        }
        logger.info("Webhook rejected!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @PostMapping
    fun post(
        @RequestHeader("x-hub-signature-256") signature: String,
        @RequestBody request: MessengerWebhookRequest,
    ): ResponseEntity<String> {
        return if (MessengerWebhookConfig.PAGE_SUBSCRIPTION == request.requestObject) {
            logger.info("Handling incoming page request!")
            validateSignature(request.asJsonString(), signature)
            request.entry.stream()
                .forEach { facebookEntry ->
                    logger.debug("Received entry payload: {}", facebookEntry)
                    DuplicateBuster.filterDuplicates(facebookEntry)?.let { facebookMessageHandler.handleMessage(it) }
                }
            ResponseEntity.ok(MessengerWebhookConfig.EVENT_RECEIVED_RESPONSE)
        } else {
            logger.warn("Unknown request object {}. Replying not found!", request.requestObject)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    private fun validateSignature(body: String, signature: String) {
        logger.debug("request={} valideres", body)
        val signatureHash = signature.split("sha256=").last()
        if (signatureHash != body.hash(messengerProperties.appSecret)) {
            logger.warn("Signatur ugyldig!")
            throw InvalidRequestSignatureException()
        }
    }

    object DuplicateBuster {
        private val logger = LoggerFactory.getLogger(javaClass)
        private val timeout = Duration.ofMinutes(2)
        private var last: String? = null
        private var lastTime: Instant = now().minus(timeout)

        fun filterDuplicates(entry: FacebookEntry): FacebookEntry? {
            return if (entry.isDuplicate())
                null
            else
                return updateLastWith(entry)
        }

        private fun FacebookEntry.isDuplicate() =
            this.identifier() != null && last == this.identifier() && now().isBefore(lastTime.plus(timeout))
                .also { if (it) logger.debug("Duplikat! (id=${this.identifier()})") }

        private fun updateLastWith(entry: FacebookEntry): FacebookEntry {
            lastTime = now()
            last = entry.identifier()
            return entry
        }

        private fun FacebookEntry.identifier() = this.messaging.firstOrNull()?.message?.mid
    }

}

fun Any.asJsonString(): String {
    return try {
        ObjectMapper().writeValueAsString(this)
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

fun String.hash(key: String): String {
    val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKeySpec)
    return Hex.encodeHexString(mac.doFinal(this.toByteArray()))
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidRequestSignatureException : RuntimeException()

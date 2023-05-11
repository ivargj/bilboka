package bilboka.integration.autosys.consumer

import bilboka.integration.autosys.dto.AutosysKjoretoyResponseDto
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AkfDatautleveringConsumer {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val client = OkHttpClient()

    private val akfDatautleveringUrl: String = System.getenv("AKF_DATAUTLEVERING_URL")

    private val apiKey: String = System.getenv("AUTOSYS_APIKEY")

    fun hentKjoretoydata(kjennemerke: String): AutosysKjoretoyResponseDto {
        logger.info("¤¤¤¤¤¤ AKF url env var: ${System.getenv("AKF_DATAUTLEVERING_URL")}")
        logger.info("AKF-url: ${akfDatautleveringUrl}")
        client.newCall(
            Request.Builder()
                .url("${akfDatautleveringUrl}?kjennemerke=$kjennemerke")
                .header("SVV-Authorization", "Apikey ${apiKey}")
                .build()
        ).execute().use { response ->
            if (response.isSuccessful) {
                logger.info("Hentet kjøretøydata for $kjennemerke")
                return response.body()?.string()
                    ?.let { mapper.readValue(it, AutosysKjoretoyResponseDto::class.java) }
                    ?.also { logResponse(it) }
                    ?: throw KjoretoydataFeiletException("Fikk svar men der var det ingen data")
            } else {
                logger.error(
                    String.format(
                        "Hent kjøretøydata for $kjennemerke gikk ikke ok. Status: %s - %s",
                        response.code(),
                        response.body()?.string()
                    )
                )
                throw KjoretoydataFeiletException(
                    "Feilrespons fra kjøretøydata (${response.code()}). ${
                        response.body()?.string()
                    }"
                )
            }
        }
    }

    private fun logResponse(kjoretoyResponseDto: AutosysKjoretoyResponseDto) {
        logger.info("Kall til AKF Datautlevering ga ${kjoretoyResponseDto.kjoretoydataListe.size} treff")
    }
}

class KjoretoydataFeiletException(message: String? = null) : RuntimeException(message)

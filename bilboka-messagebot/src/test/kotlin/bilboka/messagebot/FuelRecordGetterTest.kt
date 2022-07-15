/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package bilboka.messagebot

import bilboka.core.book.domain.FuelRecord
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FuelRecordGetterTest {

    @MockK
    lateinit var carBookExecutor: CarBookExecutor

    @MockK
    lateinit var botMessenger: BotMessenger

    @InjectMockKs
    lateinit var messagebot: MessageBot

    private val senderID = "1267"

    @BeforeEach
    fun setupMessenger() {
        justRun { botMessenger.sendMessage(any(), any()) }
    }

    @Test
    fun sendGetLastRecord_repliedWithLastRecord() {
        every { carBookExecutor.getLastRecord(any()) } returns FuelRecord(
            amount = 30.0, costNOK = 300.0
        )

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                "Siste tanking av testbil, 30.0 liter for 300.0 kr, 10.0 kr/l",
                senderID
            )
        }
        confirmVerified(botMessenger)
    }

    @Test
    fun sendGetLastRecordWhenNoRecords_repliesSomethingUseful() {
        every { carBookExecutor.getLastRecord(any()) } returns null

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                any(),
                senderID
            )
        }
        confirmVerified(botMessenger)
    }

}

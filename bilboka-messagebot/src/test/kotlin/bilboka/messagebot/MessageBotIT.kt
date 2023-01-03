package bilboka.messagebot;

import bilboka.core.book.Book
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest(classes = [MessageBot::class, TestMessenger::class, Book::class, VehicleService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageBotIT : H2Test() {

    @Autowired
    lateinit var testMessenger: TestMessenger

    @Autowired
    lateinit var messageBot: MessageBot

    @Autowired
    lateinit var vehicleService: VehicleService

    @BeforeAll
    fun setup() {
        vehicleService.addVehicle(
            name = "XC 70",
            // nicknames = setOf("xc70", "crosser"),
            fuelType = FuelType.DIESEL,
            tegnkombinasjon = "KT65881"
        )
        vehicleService.addVehicle(
            name = "en testbil",
//                nicknames = setOf("testbil"),
            fuelType = FuelType.BENSIN
        )
    }

    @AfterEach
    fun resetMock() {
        testMessenger.reset()
    }

    @Test
    fun sendAddFuelRequest() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 34567 30l 300kr",
            reply = "Registrert tanking av en testbil ved 34567 km: 30 liter for 300 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCase() {
        processMessagaAndAssertReply(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
            reply = "Registrert tanking av en testbil ved 5555 km: 30,2 liter for 302 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma() {
        processMessagaAndAssertReply(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
            reply = "Registrert tanking av xc 70 ved 1234 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestUnknownCar() {
        processMessagaAndAssertReply(
            message = "Drivstoff tullebil 34567 30l 300kr",
            reply = "Kjenner ikke til bil tullebil"
        )
    }

    @Test
    fun canSayHei() {
        processMessagaAndAssertReply(
            message = "Hei",
            reply = "Hei"
        )
    }

    private fun processMessagaAndAssertReply(message: String, reply: String) {
        messageBot.processMessage(message, "123")

        assertThat(testMessenger.messageSent).isEqualTo(reply)
        assertThat(testMessenger.recipient).isEqualTo("123")
    }

}

@Component
class TestMessenger : BotMessenger {
    override val sourceName: String
        get() = "Test messenger"

    var messageSent: String? = null
    var recipient: String? = null

    fun reset() {
        messageSent = null
        recipient = null
    }

    override fun sendMessage(message: String, recipientID: String) {
        messageSent = message
        recipient = recipientID
    }

    override fun sendPostback(options: List<String>, recipientID: String) {
        TODO("Not yet implemented")
    }

}

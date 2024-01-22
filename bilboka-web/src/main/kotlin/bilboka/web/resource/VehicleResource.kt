package bilboka.web.resource

import bilboka.client.BilbokaDataPoint
import bilboka.client.BookEntryDto
import bilboka.client.VehicleResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("vehicles")
class VehicleResource(
) {

    @GetMapping()
    fun vehicles(): ResponseEntity<List<VehicleResponse>> {
        if (SecurityContextHolder.getContext().authentication.principal != "TestUser") {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(emptyList()) // TODO
    }

    @GetMapping("sample")
    fun sample(): ResponseEntity<List<VehicleResponse>> {
        return ResponseEntity.ok(vehiclesSample().values.toList())
    }

    @GetMapping("{id}/sample")
    fun sampleById(
        @PathVariable id: String
    ): ResponseEntity<VehicleResponse> {
        return vehiclesSample()[id]?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    private fun vehiclesSample() = mapOf(
        "1" to VehicleResponse(
            id = "1",
            name = "Testbil 1",
            tegnkombinasjon = "AB12367",
            odometerUnit = "km",
            fuelType = "BENSIN",
            tankVolume = 50,
            regStatus = "REGISTRERT",
            lastOdometer = 234567,
            entriesCount = 0
        ),
        "2" to VehicleResponse(
            id = "2",
            name = "Testbil 2",
            tegnkombinasjon = "AB12345",
            odometerUnit = "mi",
            fuelType = "DIESEL",
            tankVolume = 70,
            regStatus = "AVREGISTRERT",
            lastOdometer = 123456,
            understellsnummer = "12345678901234567",
            sistePKK = LocalDate.of(2020, 1, 1),
            fristPKK = LocalDate.of(2021, 1, 1),
            regBevaringsverdig = true,
            egenvekt = 2000,
            nyttelast = 1000,
            hengervektMBrems = 2000,
            lengde = 500,
            entriesCount = 10,
            lastYearlyDifference = BilbokaDataPoint(
                dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                sourceEntryFirst = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2020, 1, 1).atStartOfDay().toString(),
                    odometer = 123456,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                sourceEntryLast = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2021, 1, 1).atStartOfDay().toString(),
                    odometer = 123656,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                )
            ),
            averageFuelConsumption = BilbokaDataPoint(
                dateTime = LocalDate.of(2020, 1, 1).atStartOfDay(),
                sourceEntryFirst = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2020, 1, 1).atStartOfDay().toString(),
                    odometer = 123456,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                sourceEntryLast = BookEntryDto(
                    id = "1",
                    type = "FUEL",
                    dateTime = LocalDate.of(2021, 1, 1).atStartOfDay().toString(),
                    odometer = 123656,
                    odometerKilometers = 123456,
                    amount = 50.0,
                    costNOK = 500.0,
                ),
                estimatedConsumptionLitersPer10Km = 1.5,
            )
        ),
        "3" to VehicleResponse(
            id = "3",
            name = "Testbil 3",
            tegnkombinasjon = "AB12389",
            odometerUnit = "km",
            fuelType = "DIESEL",
            tankVolume = 65,
            regStatus = "REGISTRERT",
            understellsnummer = "12345678901234567",
            sistePKK = LocalDate.of(2020, 1, 1),
            fristPKK = LocalDate.of(2021, 1, 1),
            regBevaringsverdig = true,
            egenvekt = 2000,
            nyttelast = 1000,
            hengervektMBrems = 2000,
            lengde = 500,
            entriesCount = 34
        ),
    )

}

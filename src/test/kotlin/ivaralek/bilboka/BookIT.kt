package ivaralek.bilboka

import ivaralek.bilboka.book.domain.FuelRecord
import ivaralek.bilboka.book.service.CarBookService
import ivaralek.bilboka.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZonedDateTime
import java.util.OptionalDouble.of

@SpringBootTest
internal class BookIT(@Autowired val carBookService: CarBookService) {

    @Test
    fun saveAndGetFromStorage() {
        val bil = Vehicle("testbil")
        carBookService.makeNewBookForVehicle(bil)

        val bookByVehicle = carBookService.getBookForVehicle(bil)
        assertThat(bookByVehicle).isNotNull
        assertThat(bookByVehicle?.vehicle).isEqualTo(bil)

        val bookByName = carBookService.getBookForVehicle(bil.name)
        assertThat(bookByName).isNotNull
        assertThat(bookByName?.vehicle).isEqualTo(bil)

        val record = FuelRecord(ZonedDateTime.now(), 1234, of(10.0), of(190.1), false)
        carBookService.addRecordForVehicle(record, bil)

        assertThat(bookByVehicle?.records).hasSize(1)
        assertThat(bookByVehicle?.records).contains(record)

    }
}
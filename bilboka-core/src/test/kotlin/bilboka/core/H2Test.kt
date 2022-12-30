package bilboka.core

import bilboka.core.book.domain.Records
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class H2Test {
    @BeforeAll
    fun setupDatabase() {
        val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(Records, Vehicles)
            commit()
        }
    }

    @AfterEach
    fun wipeDatabase() {
        transaction {
            Records.deleteAll()
            Vehicles.deleteAll()
        }
    }
}

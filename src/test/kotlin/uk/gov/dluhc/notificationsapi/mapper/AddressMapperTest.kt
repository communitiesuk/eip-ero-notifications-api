package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.messaging.models.Address as MessageAddress
import uk.gov.dluhc.notificationsapi.models.Address as ApiAddress

class AddressMapperTest {
    private val mapper = AddressMapper()

    @Test
    fun `should map correctly from api address to dto`() {
        // Given
        val api = ApiAddress(
            property = faker.address().buildingNumber(),
            street = faker.address().streetAddress(),
            locality = faker.address().secondaryAddress(),
            area = faker.address().state(),
            postcode = faker.address().postcode(),
            town = faker.address().city(),
        )

        val expected = AddressDto(
            property = api.property,
            street = api.street,
            locality = api.locality,
            area = api.area,
            postcode = api.postcode,
            town = api.town,
        )

        // When
        val dto = mapper.fromApiToDto(api)

        // Then
        assertThat(dto).isEqualTo(expected)
    }

    @Test
    fun `should map correctly from message address to dto`() {
        // Given
        val message = MessageAddress(
            property = faker.address().buildingNumber(),
            street = faker.address().streetAddress(),
            locality = faker.address().secondaryAddress(),
            area = faker.address().state(),
            postcode = faker.address().postcode(),
            town = faker.address().city(),
        )

        val expected = AddressDto(
            property = message.property,
            street = message.street,
            locality = message.locality,
            area = message.area,
            postcode = message.postcode,
            town = message.town,
        )

        // When
        val dto = mapper.fromSqsToDto(message)

        // Then
        assertThat(dto).isEqualTo(expected)
    }

    @Test
    fun `should map blank fields correctly from api to dto`() {
        // Given
        val message = ApiAddress(
            property = "         ",
            street = faker.address().streetAddress(),
            locality = "",
            town = " ",
            area = null,
            postcode = faker.address().postcode(),
        )

        val expected = AddressDto(
            property = null,
            street = message.street,
            locality = null,
            town = null,
            area = null,
            postcode = message.postcode,
        )

        // When
        val dto = mapper.fromApiToDto(message)

        // Then
        assertThat(dto).isEqualTo(expected)
    }

    @Test
    fun `should map blank fields correctly from message to dto`() {
        // Given
        val message = MessageAddress(
            property = "         ",
            street = faker.address().streetAddress(),
            locality = "",
            town = " ",
            area = null,
            postcode = faker.address().postcode(),
        )

        val expected = AddressDto(
            property = null,
            street = message.street,
            locality = null,
            town = null,
            area = null,
            postcode = message.postcode,
        )

        // When
        val dto = mapper.fromSqsToDto(message)

        // Then
        assertThat(dto).isEqualTo(expected)
    }
}

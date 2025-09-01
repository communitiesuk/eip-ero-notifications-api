package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

class AddressMapperTest {
    private val mapper = AddressMapper()

    @Test
    fun `should map correctly from api address to dto`() {
        // Given
        val api = Address(
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
}

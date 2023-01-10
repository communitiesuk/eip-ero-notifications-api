package uk.gov.dluhc.notificationsapi.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

internal class PostalAddressTest {

    @Test
    fun `should convert toPersonalisationMap`() {
        // Given
        val addressee = faker.name().firstName()
        val property = faker.address().streetName()
        val street = faker.address().buildingNumber()
        val town = faker.address().streetName()
        val area = faker.address().city()
        val locality = faker.address().state()
        val postcode = faker.address().postcode()
        val postalAddress = PostalAddress(
            addressee = addressee,
            property = property,
            street = street,
            town = town,
            area = area,
            locality = locality,
            postcode = postcode,
        )
        val expected = mapOf(
            "address_line_1" to addressee,
            "address_line_2" to property,
            "address_line_3" to street,
            "address_line_4" to town,
            "address_line_5" to area,
            "address_line_6" to locality,
            "address_line_7" to postcode,
        )

        // When
        val actual = postalAddress.toPersonalisationMap()

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}

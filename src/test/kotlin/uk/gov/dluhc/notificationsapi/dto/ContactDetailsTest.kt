package uk.gov.dluhc.notificationsapi.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.aValidLocalAuthorityName
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto

internal class ContactDetailsTest {

    @Test
    fun `should convert toPersonalisationMap`() {
        // Given
        val localAuthorityName = aValidLocalAuthorityName()
        val website = faker.internet().url()
        val email = faker.internet().emailAddress()
        val phone = faker.phoneNumber().toString()
        val property = faker.address().buildingNumber()
        val street = faker.address().streetName()
        val town = faker.address().city()
        val area = faker.address().city()
        val locality = faker.address().state()
        val postcode = faker.address().postcode()
        val contactDetails = buildContactDetailsDto(
            localAuthorityName = localAuthorityName,
            website = website,
            email = email,
            phone = phone,
            address = buildAddressDto(
                property = property,
                street = street,
                town = town,
                area = area,
                locality = locality,
                postcode = postcode,
            ),
        )
        val expected = mapOf(
            "LAName" to localAuthorityName,
            "eroPhone" to phone,
            "eroWebsite" to website,
            "eroEmail" to email,
            "eroAddressLine1" to property,
            "eroAddressLine2" to street,
            "eroAddressLine3" to town,
            "eroAddressLine4" to area,
            "eroAddressLine5" to locality,
            "eroPostcode" to postcode,
        )
        val personalisation = mutableMapOf<String, String>()

        // When
        contactDetails.mapToPersonalisation(personalisation)

        // Then
        assertThat(personalisation).isEqualTo(expected)
    }

    @Test
    fun `should convert null values toPersonalisationMap`() {
        // Given
        val localAuthorityName = aValidLocalAuthorityName()
        val website = faker.internet().url()
        val email = faker.internet().emailAddress()
        val phone = faker.phoneNumber().toString()
        val street = faker.address().streetName()
        val postcode = faker.address().postcode()
        val contactDetails = buildContactDetailsDto(
            localAuthorityName = localAuthorityName,
            website = website,
            email = email,
            phone = phone,
            address = buildAddressDto(
                property = null,
                street = street,
                town = null,
                area = null,
                locality = null,
                postcode = postcode,
            ),
        )
        val expected = mapOf(
            "LAName" to localAuthorityName,
            "eroPhone" to phone,
            "eroWebsite" to website,
            "eroEmail" to email,
            "eroAddressLine1" to "",
            "eroAddressLine2" to street,
            "eroAddressLine3" to "",
            "eroAddressLine4" to "",
            "eroAddressLine5" to "",
            "eroPostcode" to postcode,
        )
        val personalisation = mutableMapOf<String, String>()

        // When
        contactDetails.mapToPersonalisation(personalisation)

        // Then
        assertThat(personalisation).isEqualTo(expected)
    }
}

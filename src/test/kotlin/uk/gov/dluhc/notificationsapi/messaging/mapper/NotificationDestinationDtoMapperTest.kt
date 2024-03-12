package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.OverseasAddress
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressOverseasAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.messaging.models.OverseasAddress as OverseasAddressMessage

internal class NotificationDestinationDtoMapperTest {

    private val mapper: NotificationDestinationDtoMapper =
        NotificationDestinationDtoMapperImpl()

    @Test
    fun `should map SQS MessageAddress to NotificationDestinationDto for Email Address`() {
        // Given
        val email: String = anEmailAddress()

        val expectedDestination = NotificationDestinationDto(
            emailAddress = email,
            postalAddress = null,
            overseasAddress = null
        )

        val request = MessageAddress(
            emailAddress = email,
            postalAddress = null,
        )

        // When
        val destination = mapper.toNotificationDestinationDto(request)

        // Then
        assertThat(destination).isEqualTo(expectedDestination)
    }

    @Test
    fun `should map SQS MessageAddress to NotificationDestinationDto for Postal Address`() {
        // Given
        val addressee: String = faker.name().firstName()
        val property: String = faker.address().streetName()
        val street: String = faker.address().buildingNumber()
        val town: String? = faker.address().streetName()
        val area: String? = faker.address().city()
        val locality: String? = faker.address().state()
        val postcode: String = faker.address().postcode()

        val expectedDestination = NotificationDestinationDto(
            emailAddress = null,
            overseasAddress = null,
            postalAddress = PostalAddress(
                addressee = addressee,
                property = property,
                street = street,
                town = town,
                area = area,
                locality = locality,
                postcode = postcode,
            )
        )

        val request = MessageAddress(
            emailAddress = null,
            postalAddress = MessageAddressPostalAddress(
                addressee = addressee,
                address = Address(
                    property = property,
                    street = street,
                    town = town,
                    area = area,
                    locality = locality,
                    postcode = postcode
                ),
            )
        )

        // When
        val destination = mapper.toNotificationDestinationDto(request)

        // Then
        assertThat(destination).isEqualTo(expectedDestination)
    }

    @Test
    fun `should map SQS MessageAddress to NotificationDestinationDto for Overseas Address`() {
        // Given
        val addressee: String = faker.name().firstName()
        val addressLine1: String = faker.address().streetName()
        val addressLine2: String? = faker.address().buildingNumber()
        val addressLine3: String? = faker.address().streetName()
        val addressLine4: String? = faker.address().city()
        val addressLine5: String? = faker.address().state()
        val country: String = faker.address().country()

        val expectedDestination = NotificationDestinationDto(
            emailAddress = null,
            postalAddress = null,
            overseasAddress = OverseasAddress(
                addressee = addressee,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                addressLine3 = addressLine3,
                addressLine4 = addressLine4,
                addressLine5 = addressLine5,
                country = country
            )
        )

        val request = MessageAddress(
            emailAddress = null,
            overseasAddress = MessageAddressOverseasAddress(
                addressee = addressee,
                address = OverseasAddressMessage(
                    addressLine1 = addressLine1,
                    addressLine2 = addressLine2,
                    addressLine3 = addressLine3,
                    addressLine4 = addressLine4,
                    addressLine5 = addressLine5,
                    country = country
                ),
            ),
            postalAddress = null
        )

        // When
        val destination = mapper.toNotificationDestinationDto(request)

        // Then
        assertThat(destination).isEqualTo(expectedDestination)
    }
}

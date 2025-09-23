package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.aValidLocalAuthorityName
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildAddressMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails as ContactDetailsMessage

@ExtendWith(MockitoExtension::class)
class EroContactDetailsMapperTest {
    @InjectMocks
    private lateinit var mapper: EroContactDetailsMapper

    @Mock
    private lateinit var addressMapper: AddressMapper

    @Test
    fun `should map from api to dto`() {
        // Given
        val apiAddress = buildAddress()

        val api = ContactDetails(
            localAuthorityName = aValidLocalAuthorityName(),
            website = faker.internet().url(),
            email = faker.internet().safeEmailAddress(),
            phone = faker.phoneNumber().phoneNumber(),
            address = apiAddress,
        )

        val dtoAddress = buildAddressDto()

        val expected = ContactDetailsDto(
            localAuthorityName = api.localAuthorityName,
            website = api.website,
            email = api.email,
            phone = api.phone,
            address = dtoAddress,
        )

        given(addressMapper.fromApiToDto(apiAddress)).willReturn(dtoAddress)

        // When
        val dto = mapper.fromApiToDto(api)

        // Then
        assertThat(dto).isEqualTo(expected)
        verify(addressMapper).fromApiToDto(apiAddress)
    }

    @Test
    fun `should map from message to dto`() {
        // Given
        val messageAddress = buildAddressMessage()

        val api = ContactDetailsMessage(
            localAuthorityName = aValidLocalAuthorityName(),
            website = faker.internet().url(),
            email = faker.internet().safeEmailAddress(),
            phone = faker.phoneNumber().phoneNumber(),
            address = messageAddress,
        )

        val dtoAddress = buildAddressDto()

        val expected = ContactDetailsDto(
            localAuthorityName = api.localAuthorityName,
            website = api.website,
            email = api.email,
            phone = api.phone,
            address = dtoAddress,
        )

        given(addressMapper.fromSqsToDto(messageAddress)).willReturn(dtoAddress)

        // When
        val dto = mapper.fromSqsToDto(api)

        // Then
        assertThat(dto).isEqualTo(expected)
        verify(addressMapper).fromSqsToDto(messageAddress)
    }
}

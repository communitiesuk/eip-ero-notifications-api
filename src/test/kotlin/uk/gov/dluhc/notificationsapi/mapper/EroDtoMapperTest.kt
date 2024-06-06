package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.EroContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.EroDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildLocalAuthorityResponse

class EroDtoMapperTest {
    private val mapper = EroDtoMapperImpl()

    @Test
    fun `should map ERO response to ERO dto`() {
        // Given
        val localAuthority = buildLocalAuthorityResponse(contactDetailsWelsh = null)
        val expected = with(localAuthority) {
            EroDto(
                englishContactDetails = with(localAuthority.contactDetailsEnglish) {
                    EroContactDetailsDto(
                        name = name,
                        emailAddress = email,
                        phoneNumber = phone,
                        website = website,
                        address = with(address) {
                            AddressDto(
                                street = street,
                                postcode = postcode,
                                property = property,
                                locality = locality,
                                town = town,
                                area = area,
                            )
                        },
                    )
                },
                welshContactDetails = null,
            )
        }

        // When
        val actual = mapper.toEroDto(localAuthority)

        // Then
        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected)
    }

    @Test
    fun `should map ERO response with Welsh to ERO dto`() {
        // Given
        val localAuthority = buildLocalAuthorityResponse(
            contactDetailsWelsh = buildContactDetails(),
        )
        val expected = with(localAuthority) {
            EroDto(
                englishContactDetails = with(localAuthority.contactDetailsEnglish) {
                    EroContactDetailsDto(
                        name = name,
                        emailAddress = email,
                        phoneNumber = phone,
                        website = website,
                        address = with(address) {
                            AddressDto(
                                street = street,
                                postcode = postcode,
                                property = property,
                                locality = locality,
                                town = town,
                                area = area,
                            )
                        },
                    )
                },
                welshContactDetails = with(localAuthority.contactDetailsWelsh!!) {
                    EroContactDetailsDto(
                        name = name,
                        emailAddress = email,
                        phoneNumber = phone,
                        website = website,
                        address = with(address) {
                            AddressDto(
                                street = street,
                                postcode = postcode,
                                property = property,
                                locality = locality,
                                town = town,
                                area = area,
                            )
                        },
                    )
                },
            )
        }

        // When
        val actual = mapper.toEroDto(localAuthority)

        // Then
        assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected)
    }
}

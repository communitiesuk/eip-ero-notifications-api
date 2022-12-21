package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDtoWithOptionalFieldsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDto

class PhotoResubmissionPersonalisationMapperTest {

    private val mapper = PhotoResubmissionPersonalisationMapper()

    @Test
    fun `should map dto to personalisation map when all fields present`() {
        // Given
        val personalisationDto = buildPhotoResubmissionPersonalisationDto()
        val expected = buildPersonalisationMapFromDto(personalisationDto)

        // When
        val actual = mapper.toTemplatePersonalisationMap(personalisationDto)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun `should map dto to personalisation map when all optional fields not present`() {
        // Given
        val personalisationDto = buildPhotoResubmissionPersonalisationDto(
            eroContactDetails = buildContactDetailsDto(
                address = buildAddressDtoWithOptionalFieldsNull()
            )
        )
        val expected = buildPersonalisationMapFromDto(personalisationDto)

        // When
        val actual = mapper.toTemplatePersonalisationMap(personalisationDto)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        assertThat(actual["eroAddressLine1"]).isBlank
        assertThat(actual["eroAddressLine2"]).isEqualTo(personalisationDto.eroContactDetails.address.street)
        assertThat(actual["eroAddressLine3"]).isBlank
        assertThat(actual["eroAddressLine4"]).isBlank
        assertThat(actual["eroAddressLine5"]).isBlank
        assertThat(actual["eroPostcode"]).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
    }
}

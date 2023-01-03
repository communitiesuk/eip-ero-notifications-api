package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDtoWithOptionalFieldsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto

class TemplatePersonalisationDtoMapperTest {

    private val mapper = TemplatePersonalisationDtoMapper()

    @Test
    fun `should map dto to personalisation map when all fields present`() {
        // Given
        val personalisationDto = buildPhotoPersonalisationDto()
        val expected = buildPhotoPersonalisationMapFromDto(personalisationDto)

        // When
        val actual = mapper.toPhotoResubmissionTemplatePersonalisationMap(personalisationDto)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun `should map dto to personalisation map when all optional fields not present`() {
        // Given
        val personalisationDto = buildPhotoPersonalisationDto(
            eroContactDetails = buildContactDetailsDto(
                address = buildAddressDtoWithOptionalFieldsNull()
            )
        )
        val expected = buildPhotoPersonalisationMapFromDto(personalisationDto)

        // When
        val actual = mapper.toPhotoResubmissionTemplatePersonalisationMap(personalisationDto)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        assertThat(actual["eroAddressLine1"]).isBlank
        assertThat(actual["eroAddressLine2"]).isEqualTo(personalisationDto.eroContactDetails.address.street)
        assertThat(actual["eroAddressLine3"]).isBlank
        assertThat(actual["eroAddressLine4"]).isBlank
        assertThat(actual["eroAddressLine5"]).isBlank
        assertThat(actual["eroPostcode"]).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
    }

    @Nested
    inner class ToIdDocumentTemplatePersonalisationMap {

        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildIdDocumentPersonalisationDto()
            val expected = buildIdDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildIdDocumentPersonalisationDto(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildIdDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisationDto)

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
}

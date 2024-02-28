package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.exception.CountryNotFoundException
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDtoWithOptionalFieldsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationReceivedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationRejectedPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedOverseasDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedOverseasDocumentTemplatePreviewPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedSignaturePersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequestedSignaturePersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredOverseasDocumentPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredOverseasDocumentTemplatePreviewPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildApplicationRejectedPersonalisationDto

class TemplatePersonalisationDtoMapperTest {

    private val mapper = TemplatePersonalisationDtoMapper()

    @Nested
    inner class ToPhotoResubmissionTemplatePersonalisationMap {

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
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"]).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"]).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
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

    @Nested
    inner class ToIdDocumentRequiredTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildIdDocumentRequiredPersonalisationDto()
            val expected = buildIdDocumentRequiredPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toIdDocumentRequiredTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildIdDocumentRequiredPersonalisationDto(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildIdDocumentRequiredPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toIdDocumentRequiredTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"] as String).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"] as String).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToApplicationReceivedTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildApplicationReceivedPersonalisationDto()
            val expected = buildApplicationReceivedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationReceivedTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildApplicationReceivedPersonalisationDto(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildApplicationReceivedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationReceivedTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"]).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"]).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToApplicationApprovedTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildApplicationApprovedPersonalisationDto()
            val expected = buildApplicationApprovedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationApprovedTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildApplicationApprovedPersonalisationDto(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildApplicationApprovedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationApprovedTemplatePersonalisationMap(personalisationDto)

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

    @Nested
    inner class ToApplicationRejectedTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildApplicationRejectedPersonalisationDto()
            val expected = buildApplicationRejectedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationRejectedTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildApplicationRejectedPersonalisationDto(
                rejectionReasonMessage = null,
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildApplicationRejectedPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toApplicationRejectedTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"] as String).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"] as String).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToRejectedDocumentTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildRejectedDocumentPersonalisationDto()
            val expected = buildRejectedDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRejectedDocumentTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildRejectedDocumentPersonalisationDto(
                rejectedDocumentFreeText = null,
                documents = emptyList(),
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildRejectedDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRejectedDocumentTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["rejectedDocuments"] as List<*>).isEmpty()
            assertThat(actual["rejectionReason"] as String?).isBlank
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"] as String).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"] as String).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToRejectedSignatureTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildRejectedSignaturePersonalisationDto()
            val expected = buildRejectedSignaturePersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRejectedSignatureTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildRejectedSignaturePersonalisationDto(
                rejectionNotes = null,
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildRejectedSignaturePersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRejectedSignatureTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["rejectionReasons"] as List<*>).isEmpty()
            assertThat(actual["rejectionNotes"] as String?).isBlank
            assertThat(actual["rejectionFreeText"] as String?).isBlank
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"] as String).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"] as String).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToRequestedSignatureTemplatePersonalisationMap {
        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildRequestedSignaturePersonalisationDto()
            val expected = buildRequestedSignaturePersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRequestedSignatureTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map dto to personalisation map when all optional fields not present`() {
            // Given
            val personalisationDto = buildRequestedSignaturePersonalisationDto(
                freeText = null,
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDtoWithOptionalFieldsNull()
                )
            )
            val expected = buildRequestedSignaturePersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRequestedSignatureTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            assertThat(actual["freeText"] as String?).isBlank
            assertThat(actual["eroAddressLine1"] as String).isBlank
            assertThat(actual["eroAddressLine2"] as String).isEqualTo(personalisationDto.eroContactDetails.address.street)
            assertThat(actual["eroAddressLine3"] as String).isBlank
            assertThat(actual["eroAddressLine4"] as String).isBlank
            assertThat(actual["eroAddressLine5"] as String).isBlank
            assertThat(actual["eroPostcode"] as String).isEqualTo(personalisationDto.eroContactDetails.address.postcode)
        }
    }

    @Nested
    inner class ToRejectedOverseasDocumentTemplatePersonalisationMap {

        private val countryNotFoundException = "Country is required to process a template for overseas"

        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildRejectedOverseasDocumentTemplatePreviewPersonalisation()
            val expected = buildRejectedOverseasDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRejectedOverseasDocumentTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map country to be the last field in the personalisation`() {
            // Given
            val personalisationDto = buildRejectedOverseasDocumentTemplatePreviewPersonalisation()
            val expectedLastValue = personalisationDto.eroContactDetails.address.country

            // When
            val actual = mapper.toRejectedOverseasDocumentTemplatePersonalisationMap(personalisationDto)
            val lastValueOfActual = actual.entries.lastOrNull()?.value

            // Then
            assertThat(lastValueOfActual).isEqualTo(expectedLastValue)
        }

        @Test
        fun `should throw an error if country is not present`() {
            // Given
            val personalisationDto = buildRejectedOverseasDocumentTemplatePreviewPersonalisation(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDto(country = null)
                )
            )

            // When
            val exception = Assertions.catchThrowableOfType(
                { mapper.toRejectedOverseasDocumentTemplatePersonalisationMap(personalisationDto) },
                CountryNotFoundException::class.java
            )

            // Then
            assertThat(exception).isNotNull()
            assertThat(exception.message).isEqualTo(countryNotFoundException)
        }
    }

    @Nested
    inner class ToRequiredOverseasDocumentTemplatePersonalisationMap {

        private val countryNotFoundException = "Country is required to process a template for overseas"

        @Test
        fun `should map dto to personalisation map when all fields present`() {
            // Given
            val personalisationDto = buildRequiredOverseasDocumentTemplatePreviewPersonalisation()
            val expected = buildRequiredOverseasDocumentPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toRequiredOverseasDocumentTemplatePersonalisationMap(personalisationDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        fun `should map country to be the last field in the personalisation`() {
            // Given
            val personalisationDto = buildRequiredOverseasDocumentTemplatePreviewPersonalisation()
            val expectedLastValue = personalisationDto.eroContactDetails.address.country

            // When
            val actual = mapper.toRequiredOverseasDocumentTemplatePersonalisationMap(personalisationDto)
            val lastValueOfActual = actual.entries.lastOrNull()?.value

            // Then
            assertThat(lastValueOfActual).isEqualTo(expectedLastValue)
        }

        @Test
        fun `should throw an error if country is not present`() {
            // Given
            val personalisationDto = buildRequiredOverseasDocumentTemplatePreviewPersonalisation(
                eroContactDetails = buildContactDetailsDto(
                    address = buildAddressDto(country = null)
                )
            )

            // When
            val exception = Assertions.catchThrowableOfType(
                { mapper.toRequiredOverseasDocumentTemplatePersonalisationMap(personalisationDto) },
                CountryNotFoundException::class.java
            )

            // Then
            assertThat(exception).isNotNull()
            assertThat(exception.message).isEqualTo(countryNotFoundException)
        }
    }
}

package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.*
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
                    address = buildAddressDtoWithOptionalFieldsNull(),
                ),
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
    }

    @Nested
    inner class ToRequiredOverseasDocumentTemplatePersonalisationMap {

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
    }

    @Nested
    inner class ToRequiredDocumentTemplatePersonalisationMap {

        @ParameterizedTest
        @CsvSource(
            value = [
                "postal, POSTAL",
                "proxy, PROXY",
                "overseas, OVERSEAS",
            ],
        )
        fun `should map dto to personalisation map when all fields present`(
            personalisationSourceTypeString: String,
            sourceTypeDto: SourceType,
        ) {
            // Given
            val personalisationDto =
                buildRequiredDocumentPersonalisation(personalisationSourceTypeString = personalisationSourceTypeString)
            val expected = buildRequiredDocumentPersonalisationMapFromDto(personalisationDto, sourceTypeDto)

            // When
            val actual = mapper.toRequiredDocumentTemplatePersonalisationMap(personalisationDto, sourceTypeDto)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }
    }

    @Nested
    inner class ToBespokeCommTemplatePersonalisationMap {

        @ParameterizedTest
        @CsvSource(
            value = [
                "postal vote, POSTAL",
                "proxy vote, PROXY",
                "overseas vote, OVERSEAS",
                "Voter Authority Certificate, VOTER_CARD",
            ],
        )
        fun `should map dto to personalisation map when all fields present`(
            personalisationSourceTypeString: String,
            sourceTypeDto: SourceType,
        ) {
            // Given
            val personalisationDto = buildBespokeCommPersonalisationDto()
            val expected = buildBespokeCommPersonalisationMapFromDto(personalisationDto)

            // When
            val actual = mapper.toBespokeCommTemplatePersonalisationMap(personalisationDto, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }
    }
}

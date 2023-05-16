package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationReceivedPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationApprovedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationReceivedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentRequiredPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocumentPersonalisationMessage

@ExtendWith(MockitoExtension::class)
internal class TemplatePersonalisationMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: TemplatePersonalisationMessageMapperImpl

    @Mock
    private lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Mock
    private lateinit var rejectedDocumentMapper: RejectedDocumentMapper

    @Nested
    inner class ToPhotoPersonalisationDto {
        @Test
        fun `should map SQS PhotoResubmissionPersonalisation to PhotoPersonalisationDto`() {
            // Given
            val personalisationMessage = buildPhotoPersonalisationMessage()
            val expectedPersonalisationDto = buildPhotoPersonalisationDtoFromMessage(personalisationMessage)

            // When
            val actual = mapper.toPhotoPersonalisationDto(personalisationMessage)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToIdDocumentPersonalisationDto {
        @Test
        fun `should map SQS IdDocumentPersonalisation to IdDocumentPersonalisationDto`() {
            // Given
            val personalisationMessage = buildIdDocumentPersonalisationMessage()
            val expectedPersonalisationDto = buildIdDocumentPersonalisationDtoFromMessage(personalisationMessage)

            // When
            val actual = mapper.toIdDocumentPersonalisationDto(personalisationMessage)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToIdDocumentRequiredPersonalisationDto {
        @Test
        fun `should map SQS IdDocumentRequiredPersonalisation to IdDocumentRequiredPersonalisationDto`() {
            // Given
            val personalisationMessage = buildIdDocumentRequiredPersonalisationMessage()
            val expectedPersonalisationDto = buildIdDocumentRequiredPersonalisationDtoFromMessage(personalisationMessage)

            // When
            val actual = mapper.toIdDocumentRequiredPersonalisationDto(personalisationMessage)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToReceivedPersonalisationDto {
        @Test
        fun `should map SQS Application Received Personalisation to ApplicationReceivedPersonalisationDto`() {
            // Given
            val personalisationMessage = buildApplicationReceivedPersonalisation()
            val expectedPersonalisationDto =
                buildApplicationReceivedPersonalisationDtoFromMessage(personalisationMessage)

            // When
            val actual = mapper.toReceivedPersonalisationDto(personalisationMessage)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToApprovedPersonalisationDto {
        @Test
        fun `should map SQS Application Approved Personalisation to ApplicationApprovedPersonalisationDto`() {
            // Given
            val personalisationMessage = buildApplicationApprovedPersonalisation()
            val expectedPersonalisationDto =
                buildApplicationApprovedPersonalisationDtoFromMessage(personalisationMessage)

            // When
            val actual = mapper.toApprovedPersonalisationDto(personalisationMessage)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToRejectedPersonalisationDto {
        @Test
        fun `should map SQS Application Rejected Personalisation to ApplicationRejectedPersonalisationDto`() {
            // Given
            val personalisationMessage = buildApplicationRejectedPersonalisation()
            val incompleteApplication = "Application is incomplete"
            val applicantHasNotResponded = "Applicant has not responded to requests for information"
            val other = "other"

            given(
                applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                    INCOMPLETE_MINUS_APPLICATION,
                    ENGLISH
                )
            )
                .willReturn(incompleteApplication)
            given(
                applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                    NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT,
                    ENGLISH
                )
            )
                .willReturn(applicantHasNotResponded)
            given(applicationRejectionReasonMapper.toApplicationRejectionReasonString(OTHER, ENGLISH)).willReturn(other)

            val expectedPersonalisationDto = with(personalisationMessage) {
                ApplicationRejectedPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    rejectionReasonList = listOf(incompleteApplication, applicantHasNotResponded, other),
                    rejectionReasonMessage = rejectionReasonMessage,
                    eroContactDetails = with(eroContactDetails) {
                        buildContactDetailsDto(
                            localAuthorityName = localAuthorityName,
                            website = website,
                            phone = phone,
                            email = email,
                            address = with(address) {
                                buildAddressDto(
                                    street = street,
                                    property = property,
                                    locality = locality,
                                    town = town,
                                    area = area,
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }

            // When
            val actual = mapper.toRejectedPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(applicationRejectionReasonMapper).toApplicationRejectionReasonString(
                INCOMPLETE_MINUS_APPLICATION,
                ENGLISH
            )
            verify(applicationRejectionReasonMapper)
                .toApplicationRejectionReasonString(NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, ENGLISH)
            verify(applicationRejectionReasonMapper).toApplicationRejectionReasonString(OTHER, ENGLISH)
            verifyNoMoreInteractions(applicationRejectionReasonMapper)
        }
    }

    @Nested
    inner class ToRejectedDocumentPersonalisationDto {
        @Test
        fun `should map SQS RejectedDocumentPersonalisation to RejectedDocumentPersonalisationDto`() {
            // Given
            val doc = buildRejectedDocument()
            val personalisationMessage = buildRejectedDocumentPersonalisationMessage(documents = listOf(doc))
            val docString = "Driving Licence - unreadable"
            val expectedPersonalisationDto = with(personalisationMessage) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf(docString),
                    rejectedDocumentFreeText = rejectedDocumentMessage,
                    eroContactDetails = with(eroContactDetails) {
                        buildContactDetailsDto(
                            localAuthorityName = localAuthorityName,
                            website = website,
                            phone = phone,
                            email = email,
                            address = with(address) {
                                buildAddressDto(
                                    street = street,
                                    property = property,
                                    locality = locality,
                                    town = town,
                                    area = area,
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }
            val language = ENGLISH
            given(rejectedDocumentMapper.fromMessagingRejectedDocumentToString(language, doc)).willReturn(docString)

            // When
            val actual = mapper.toRejectedDocumentPersonalisationDto(personalisationMessage, language)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }
}

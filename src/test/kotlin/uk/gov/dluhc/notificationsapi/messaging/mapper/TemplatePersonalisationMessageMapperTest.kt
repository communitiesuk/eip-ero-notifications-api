package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.IdentityDocumentResubmissionDocumentRejectionTextMapper
import uk.gov.dluhc.notificationsapi.mapper.PhotoRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentsMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocumentsPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedSignaturePersonalisation

@ExtendWith(MockitoExtension::class)
internal class TemplatePersonalisationMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: TemplatePersonalisationMessageMapperImpl

    @Mock
    private lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Mock
    private lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Mock
    private lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Mock
    private lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Nested
    inner class ToPhotoPersonalisationDto {
        @Test
        fun `should map SQS PhotoResubmissionPersonalisation to PhotoPersonalisationDto given no rejection reasons`() {
            // Given
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = null,
            )
            val photoRejectionReasons: List<String> = emptyList()
            val expectedPersonalisationDto = buildPhotoPersonalisationDtoFromMessage(
                personalisationMessage,
                photoRejectionReasons
            )

            // When
            val actual = mapper.toPhotoPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verifyNoInteractions(photoRejectionReasonMapper)
        }

        @Test
        fun `should map SQS PhotoResubmissionPersonalisation to PhotoPersonalisationDto given rejection reasons`() {
            // Given
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = listOf(
                    NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
                    WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
                    PhotoRejectionReason.OTHER // OTHER is deliberately excluded from the photo rejection reason mapping
                ),
                photoRejectionNotes = "Please take a head and shoulders photo, with a plain expression, and without sunglasses. Regular prescription glasses are acceptable."
            )
            val photoRejectionReasons: List<String> = listOf(
                "Not a plain facial expression",
                "Wearing sunglasses, or tinted glasses",
                // a mapping from OTHER is not expected - this is by design
            )
            val expectedPersonalisationDto = buildPhotoPersonalisationDtoFromMessage(
                personalisationMessage,
                photoRejectionReasons
            )

            given(photoRejectionReasonMapper.toPhotoRejectionReasonString(any<PhotoRejectionReason>(), any())).willReturn(
                "Not a plain facial expression",
                "Wearing sunglasses, or tinted glasses"
            )

            // When
            val actual = mapper.toPhotoPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
                NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
                ENGLISH
            )
            verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
                WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
                ENGLISH
            )
        }
    }

    @Nested
    inner class ToIdDocumentPersonalisationDto {
        @Test
        fun `should map SQS IdDocumentPersonalisation to IdDocumentPersonalisationDto`() {
            // Given
            val personalisationMessage = buildIdDocumentPersonalisationMessage()
            val channel = NotificationChannel.EMAIL

            val documentRejectionText = """
                Utility Bill
                
                * The document is too old
                
                ----
            
            """.trimIndent()
            given(documentRejectionTextMapper.toDocumentRejectionText(any(), any<IdDocumentPersonalisation>(), any()))
                .willReturn(documentRejectionText)

            val expectedPersonalisationDto = buildIdDocumentPersonalisationDtoFromMessage(personalisationMessage, documentRejectionText)

            // When
            val actual = mapper.toIdDocumentPersonalisationDto(personalisationMessage, ENGLISH, NotificationChannel.EMAIL)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(documentRejectionTextMapper).toDocumentRejectionText(ENGLISH, personalisationMessage, channel)
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
        fun `should map SQS Rejected Document Personalisation to RejectedDocumentPersonalisationDto`() {
            // Given
            val personalisationMessage = buildRejectedDocumentsPersonalisation()
            given(rejectedDocumentsMapper.mapRejectionDocumentsFromMessaging(ENGLISH, personalisationMessage.documents)).willReturn(listOf("Doc1", "Doc2"))

            val expectedPersonalisationDto = with(personalisationMessage) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf("Doc1", "Doc2"),
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

            // When
            val actual = mapper.toRejectedDocumentPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToRejectedSignaturePersonalisationDto {
        @Test
        fun `should map SQS RejectedSignaturePersonalisation to RejectedSignaturePersonalisationDto`() {
            // Given
            val rejectionReasons = listOf("Reason1", "Reason2")
            val rejectionNotes = "Invalid Signature"
            val personalisationMessage = buildRejectedSignaturePersonalisation(
                rejectionReasons = rejectionReasons,
                rejectionNotes = rejectionNotes
            )

            // When
            val actual = mapper.toRejectedSignaturePersonalisationDto(personalisationMessage)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(personalisationMessage)
        }
    }
}

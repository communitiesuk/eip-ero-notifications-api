package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.DeadlineMapper
import uk.gov.dluhc.notificationsapi.mapper.IdentityDocumentResubmissionDocumentRejectionTextMapper
import uk.gov.dluhc.notificationsapi.mapper.PhotoRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentsMapper
import uk.gov.dluhc.notificationsapi.mapper.SignatureRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentRequiredPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationApprovedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationReceivedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildBespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentRequiredPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildInviteToRegisterPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocumentsPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRequestedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRequiredDocumentPersonalisation

@ExtendWith(MockitoExtension::class)
internal class TemplatePersonalisationMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: TemplatePersonalisationMessageMapperImpl

    @Mock
    private lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Mock
    private lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Mock
    private lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Mock
    private lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Mock
    private lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var deadlineMapper: DeadlineMapper

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
                photoRejectionReasons,
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
                    PhotoRejectionReason.OTHER, // OTHER is deliberately excluded from the photo rejection reason mapping
                ),
                photoRejectionNotes = "Please take a head and shoulders photo, with a plain expression, and without sunglasses. Regular prescription glasses are acceptable.",
            )
            val photoRejectionReasons: List<String> = listOf(
                "Not a plain facial expression",
                "Wearing sunglasses, or tinted glasses",
                // a mapping from OTHER is not expected - this is by design
            )
            val expectedPersonalisationDto = buildPhotoPersonalisationDtoFromMessage(
                personalisationMessage,
                photoRejectionReasons,
            )

            given(
                photoRejectionReasonMapper.toPhotoRejectionReasonString(
                    any<PhotoRejectionReason>(),
                    any(),
                ),
            ).willReturn(
                "Not a plain facial expression",
                "Wearing sunglasses, or tinted glasses",
            )

            // When
            val actual = mapper.toPhotoPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
                NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
                ENGLISH,
            )
            verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
                WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
                ENGLISH,
            )
        }
    }

    @Nested
    inner class ToIdDocumentPersonalisationDto {
        @Test
        fun `should map SQS IdDocumentPersonalisation to IdDocumentPersonalisationDto`() {
            // Given
            val personalisationMessage = buildIdDocumentPersonalisationMessage()
            val channel = CommunicationChannel.EMAIL

            val documentRejectionText = """
                Utility Bill
                
                * The document is too old
                
                ----
            
            """.trimIndent()
            given(documentRejectionTextMapper.toDocumentRejectionText(any(), any<IdDocumentPersonalisation>(), any()))
                .willReturn(documentRejectionText)

            val expectedPersonalisationDto =
                buildIdDocumentPersonalisationDtoFromMessage(personalisationMessage, documentRejectionText)

            // When
            val actual =
                mapper.toIdDocumentPersonalisationDto(personalisationMessage, ENGLISH, CommunicationChannel.EMAIL)

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
            val expectedPersonalisationDto =
                buildIdDocumentRequiredPersonalisationDtoFromMessage(personalisationMessage)

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

            given(sourceTypeMapper.toSourceTypeString(SourceType.POSTAL, ENGLISH)).willReturn("Mapped source type")

            val expectedPersonalisationDto = with(personalisationMessage) {
                ApplicationReceivedPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            }

            // When
            val actual = mapper.toReceivedPersonalisationDto(personalisationMessage, ENGLISH, SourceType.POSTAL)

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
                    ENGLISH,
                ),
            )
                .willReturn(incompleteApplication)
            given(
                applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                    NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT,
                    ENGLISH,
                ),
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                )
            }

            // When
            val actual = mapper.toRejectedPersonalisationDto(personalisationMessage, ENGLISH)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(applicationRejectionReasonMapper).toApplicationRejectionReasonString(
                INCOMPLETE_MINUS_APPLICATION,
                ENGLISH,
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
            given(
                rejectedDocumentsMapper.mapRejectionDocumentsFromMessaging(
                    ENGLISH,
                    personalisationMessage.documents,
                ),
            ).willReturn(listOf("Doc1", "Doc2"))
            given(sourceTypeMapper.toSourceTypeString(SourceType.POSTAL, ENGLISH)).willReturn("Mapped source type")

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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            }

            // When
            val actual = mapper.toRejectedDocumentPersonalisationDto(personalisationMessage, ENGLISH, SourceType.POSTAL)

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToRejectedSignaturePersonalisationDto {
        @Test
        fun `should map SQS RejectedSignaturePersonalisation to RejectedSignaturePersonalisationDto`() {
            // Given
            val personalisationMessage = buildRejectedSignaturePersonalisation()
            val partiallyCutOff = "The image has some of it cut off"
            val tooDark = "The image is too dark"
            given(
                signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                    SignatureRejectionReason.PARTIALLY_MINUS_CUT_MINUS_OFF,
                    ENGLISH,
                ),
            )
                .willReturn(partiallyCutOff)
            given(
                signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                    SignatureRejectionReason.TOO_MINUS_DARK,
                    ENGLISH,
                ),
            )
                .willReturn(tooDark)

            val expectedRejectionReasons = listOf(
                partiallyCutOff,
                tooDark,
                // a mapping from OTHER is not expected - this is by design
            )
            given(sourceTypeMapper.toSourceTypeString(SourceType.POSTAL, ENGLISH)).willReturn("Mapped source type")

            val expectedPersonalisationDto = with(personalisationMessage) {
                RejectedSignaturePersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    rejectionReasons = expectedRejectionReasons,
                    rejectionNotes = rejectionNotes,
                    rejectionFreeText = null,
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            }

            // When
            val actual =
                mapper.toRejectedSignaturePersonalisationDto(personalisationMessage, ENGLISH, SourceType.POSTAL)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
            verify(signatureRejectionReasonMapper).toSignatureRejectionReasonString(
                SignatureRejectionReason.PARTIALLY_MINUS_CUT_MINUS_OFF,
                ENGLISH,
            )
            verify(signatureRejectionReasonMapper).toSignatureRejectionReasonString(
                SignatureRejectionReason.TOO_MINUS_DARK,
                ENGLISH,
            )
            verifyNoMoreInteractions(signatureRejectionReasonMapper)
        }
    }

    @Nested
    inner class ToRequestedSignaturePersonalisationDto {
        @Test
        fun `should map SQS RequestedSignaturePersonalisation to RequestedSignaturePersonalisationDto`() {
            // Given
            val personalisationMessage = buildRequestedSignaturePersonalisation()

            given(sourceTypeMapper.toSourceTypeString(SourceType.POSTAL, ENGLISH)).willReturn("Mapped source type")

            val expectedPersonalisationDto = with(personalisationMessage) {
                RequestedSignaturePersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    freeText = freeText,
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            }

            // When
            val actual =
                mapper.toRequestedSignaturePersonalisationDto(personalisationMessage, ENGLISH, SourceType.POSTAL)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToRequiredDocumentTemplatePersonalisationDto {
        @ParameterizedTest
        @EnumSource(SourceType::class)
        fun `should map SQS RequiredDocumentTemplatePersonalisation to RequiredDocumentTemplatePersonalisationDto`(
            sourceType: SourceType,
        ) {
            // Given
            val personalisationMessage = buildRequiredDocumentPersonalisation()

            given(sourceTypeMapper.toSourceTypeString(sourceType, ENGLISH)).willReturn("Mapped source type")

            val expectedPersonalisationDto = with(personalisationMessage) {
                RequiredDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    additionalNotes = additionalNotes,
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            }

            // When
            val actual =
                mapper.toRequiredDocumentTemplatePersonalisationDto(personalisationMessage, ENGLISH, sourceType)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToBespokeCommTemplatePersonalisationDto {
        @ParameterizedTest
        @EnumSource(SourceType::class)
        fun `should map SQS BespokeCommTemplatePersonalisation to BespokeCommTemplatePersonalisationDto`(
            sourceType: SourceType,
        ) {
            // Given
            val personalisationMessage = buildBespokeCommPersonalisation()

            given(sourceTypeMapper.toFullSourceTypeString(sourceType, ENGLISH)).willReturn("Full mapped source type")
            given(deadlineMapper.toDeadlineString(any(), any(), any(), any())).willReturn("Mapped deadline")

            val expectedPersonalisationDto = with(personalisationMessage) {
                BespokeCommPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationFullSourceTypeString = "Full mapped source type",
                    subjectHeader = subjectHeader,
                    details = details,
                    whatToDo = whatToDo,
                    deadline = "Mapped deadline",
                )
            }

            // When
            val actual =
                mapper.toBespokeCommTemplatePersonalisationDto(personalisationMessage, ENGLISH, sourceType)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }

    @Nested
    inner class ToInviteToRegisterTemplatePersonalisationDto {
        @ParameterizedTest
        @CsvSource(
                value = [
                    "VOTER_MINUS_CARD",
                    "PROXY",
                    "POSTAL",
                ],
        )
        fun `should map SQS InviteToRegisterTemplatePersonalisation to InviteToRegisterTemplatePersonalisationDto`(
                sourceType: SourceType,
        ) {
            // Given
            val personalisationMessage = buildInviteToRegisterPersonalisation()

            given(sourceTypeMapper.toFullSourceTypeString(sourceType, ENGLISH)).willReturn("Full mapped source type")

            val expectedPersonalisationDto = with(personalisationMessage) {
                InviteToRegisterPersonalisationDto(
                        applicationReference = applicationReference,
                        firstName = firstName,
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
                                                postcode = postcode,
                                        )
                                    },
                            )
                        },
                        personalisationFullSourceTypeString = "Full mapped source type",
                        freeText = freeText,
                )
            }

            // When
            val actual =
                    mapper.toInviteToRegisterTemplatePersonalisationDto(personalisationMessage, ENGLISH, sourceType)
            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationDto)
        }
    }
}

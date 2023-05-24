package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationChannelMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DUPLICATE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.INVALID_MINUS_DOCUMENT_MINUS_COUNTRY
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.INVALID_MINUS_DOCUMENT_MINUS_TYPE
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason.WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotificationDestination
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyPhotoResubmissionMessage
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageMapper_NotificationTypeTest {

    @InjectMocks
    private lateinit var mapper: SendNotifyMessageMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationDestinationDtoMapper: NotificationDestinationDtoMapper

    companion object {
        @JvmStatic
        fun photoRejectionReasons_to_NotificationType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(emptyList<PhotoRejectionReason>(), PHOTO_RESUBMISSION),
                Arguments.of(listOf(OTHER), PHOTO_RESUBMISSION),
                Arguments.of(listOf(OTHER), PHOTO_RESUBMISSION),

                Arguments.of(
                    listOf(
                        NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),

                Arguments.of(
                    listOf(
                        OTHER,
                        NOT_MINUS_FACING_MINUS_FORWARDS_MINUS_OR_MINUS_LOOKING_MINUS_AT_MINUS_THE_MINUS_CAMERA
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_NOT_MINUS_IN_MINUS_COLOUR_MINUS_DISTORTED_MINUS_OR_MINUS_TOO_MINUS_DARK
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(OTHER, OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(OTHER, NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        EYES_MINUS_NOT_MINUS_OPEN_MINUS_OR_MINUS_VISIBLE_MINUS_OR_MINUS_HAIR_MINUS_IN_MINUS_FRONT_MINUS_FACE
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(OTHER, WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_HAS_MINUS_HEAD_MINUS_COVERING_MINUS_ASIDE_MINUS_FROM_MINUS_RELIGIOUS_MINUS_OR_MINUS_MEDICAL
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(
                        OTHER,
                        PHOTO_MINUS_HAS_MINUS_RED_MINUS_EYE_MINUS_GLARE_MINUS_OR_MINUS_SHADOWS_MINUS_OVER_MINUS_FACE
                    ),
                    PHOTO_RESUBMISSION_WITH_REASONS
                ),
            )
        }

        @JvmStatic
        fun documentRejectionReasons_to_NotificationType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(emptyList<DocumentRejectionReason>(), ID_DOCUMENT_RESUBMISSION),
                Arguments.of(listOf(DocumentRejectionReason.OTHER), ID_DOCUMENT_RESUBMISSION),
                Arguments.of(listOf(DocumentRejectionReason.OTHER), ID_DOCUMENT_RESUBMISSION),

                Arguments.of(
                    listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(UNREADABLE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(INVALID_MINUS_DOCUMENT_MINUS_TYPE),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DUPLICATE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(INVALID_MINUS_DOCUMENT_MINUS_COUNTRY),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),

                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, DOCUMENT_MINUS_TOO_MINUS_OLD),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, UNREADABLE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, INVALID_MINUS_DOCUMENT_MINUS_TYPE),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, DUPLICATE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, INVALID_MINUS_DOCUMENT_MINUS_COUNTRY),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
                Arguments.of(
                    listOf(DocumentRejectionReason.OTHER, APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS
                ),
            )
        }
    }

    @Nested
    inner class FromPhotoMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @MethodSource("uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper_NotificationTypeTest#photoRejectionReasons_to_NotificationType")
        fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto with correct NotificationType mapping given rejection reasons and no rejection notes`(
            photoRejectionReasons: List<PhotoRejectionReason>,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val request = buildSendNotifyPhotoResubmissionMessage(
                personalisation = buildPhotoPersonalisationMessage(
                    photoRejectionReasons = photoRejectionReasons,
                    photoRejectionNotes = null
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val notification = mapper.fromPhotoMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                ", PHOTO_RESUBMISSION",
                "'', PHOTO_RESUBMISSION",
                "'Some rejection reason notes', PHOTO_RESUBMISSION_WITH_REASONS",
            ]
        )
        fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto with correct NotificationType mapping given no rejection reasons and rejection notes`(
            photoRejectionNotes: String?,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val request = buildSendNotifyPhotoResubmissionMessage(
                personalisation = buildPhotoPersonalisationMessage(
                    photoRejectionReasons = emptyList(),
                    photoRejectionNotes = photoRejectionNotes
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val notification = mapper.fromPhotoMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        }
    }

    @Nested
    inner class FromIdDocumentMessageToSendNotificationRequestDto {

        @ParameterizedTest
        @MethodSource("uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper_NotificationTypeTest#documentRejectionReasons_to_NotificationType")
        fun `should map ID document template request to dto with correct NotificationType mapping given rejection reasons and no rejection notes`(
            documentRejectionReasons: List<DocumentRejectionReason>,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val request = buildSendNotifyIdDocumentResubmissionMessage(
                personalisation = buildIdDocumentPersonalisationMessage(
                    rejectedDocuments = listOf(
                        buildRejectedDocument(
                            rejectionReasons = documentRejectionReasons,
                            rejectionNotes = null
                        )
                    )
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val actual = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                ", ID_DOCUMENT_RESUBMISSION",
                "'', ID_DOCUMENT_RESUBMISSION",
                "'Some rejection reason notes', ID_DOCUMENT_RESUBMISSION_WITH_REASONS",
            ]
        )
        fun `should map ID document template request to dto with correct NotificationType mapping given document with no rejection reasons and rejection notes`(
            documentRejectionNotes: String?,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val request = buildSendNotifyIdDocumentResubmissionMessage(
                personalisation = buildIdDocumentPersonalisationMessage(
                    rejectedDocuments = listOf(
                        buildRejectedDocument(
                            rejectionReasons = emptyList(),
                            rejectionNotes = documentRejectionNotes
                        )
                    )
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val actual = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
        }

        @Test
        fun `should map ID document template request to dto with correct NotificationType mapping given all documents have rejection reasons or rejection notes`() {
            // Given
            val request = buildSendNotifyIdDocumentResubmissionMessage(
                personalisation = buildIdDocumentPersonalisationMessage(
                    rejectedDocuments = listOf(
                        buildRejectedDocument(
                            rejectionReasons = emptyList(),
                            rejectionNotes = "a reason for rejecting the document"
                        ),
                        buildRejectedDocument(
                            rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                            rejectionNotes = null
                        )
                    )
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val actual = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(actual.notificationType).isEqualTo(ID_DOCUMENT_RESUBMISSION_WITH_REASONS)
        }

        @Test
        fun `should map ID document template request to dto with correct NotificationType mapping given not all documents have rejection reasons or rejection notes`() {
            // Given
            val request = buildSendNotifyIdDocumentResubmissionMessage(
                personalisation = buildIdDocumentPersonalisationMessage(
                    rejectedDocuments = listOf(
                        buildRejectedDocument(
                            rejectionReasons = emptyList(),
                            rejectionNotes = "a reason for rejecting the document"
                        ),
                        buildRejectedDocument(
                            rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                            rejectionNotes = null
                        ),
                        buildRejectedDocument(
                            rejectionReasons = emptyList(),
                            rejectionNotes = null
                        )
                    )
                )
            )

            given(languageMapper.fromMessageToDto(any())).willReturn(ENGLISH)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(aSourceType())
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any()))
                .willReturn(aNotificationDestination())
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(aNotificationChannel())

            // When
            val actual = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(actual.notificationType).isEqualTo(ID_DOCUMENT_RESUBMISSION)
        }
    }
}

package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.NINO_NOT_MATCHED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_SIGNATURE
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationChannelMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyNinoNotMatchedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedDocumentMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotificationDestination
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.aMessageAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationApprovedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationReceivedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentRequiredPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocumentsPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedSignatureMessage
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel as SqsChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageMapperTest {

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

    @Nested
    inner class FromPhotoMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto given no rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = PHOTO_RESUBMISSION
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = null
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

            val request = buildSendNotifyPhotoResubmissionMessage(
                channel = SqsChannel.EMAIL,
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromPhotoMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }

        @Test
        fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto given rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = PHOTO_RESUBMISSION_WITH_REASONS
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = listOf(PhotoRejectionReason.OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO)
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

            val request = buildSendNotifyPhotoResubmissionMessage(
                channel = SqsChannel.EMAIL,
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromPhotoMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }
    }

    @Nested
    inner class FromIdDocumentMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyIdDocumentResubmissionMessage to SendNotificationRequestDto given no document rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
            val personalisationMessage = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = emptyList()
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

            val request = SendNotifyIdDocumentResubmissionMessage(
                channel = SqsChannel.EMAIL,
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }

        @Test
        fun `should map SQS SendNotifyIdDocumentResubmissionMessage to SendNotificationRequestDto given document rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            val personalisationMessage = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD)
                    )
                )
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

            val request = SendNotifyIdDocumentResubmissionMessage(
                channel = SqsChannel.EMAIL,
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }
    }

    @Nested
    inner class FromIdDocumentRequiredMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyIdDocumentRequiredMessage to SendNotificationRequestDto`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
            val personalisationMessage = buildIdDocumentRequiredPersonalisationMessage()
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

            val request = SendNotifyIdDocumentRequiredMessage(
                channel = SqsChannel.EMAIL,
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromIdDocumentRequiredMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }
    }

    @Nested
    inner class FromReceivedMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyApplicationReceivedMessage to SendNotificationRequestDto`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.POSTAL
            val expectedNotificationType = NotificationType.APPLICATION_RECEIVED
            val personalisation = buildApplicationReceivedPersonalisation()
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

            val request = SendNotifyApplicationReceivedMessage(
                language = Language.EN,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.APPLICATION_MINUS_RECEIVED,
                toAddress = toAddress,
                personalisation = personalisation,
            )

            // When
            val notification = mapper.fromReceivedMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.APPLICATION_MINUS_RECEIVED)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.POSTAL)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
        }
    }

    @Nested
    inner class FromApprovedMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyApplicationApprovedMessage to SendNotificationRequestDto`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = NotificationType.APPLICATION_APPROVED
            val personalisation = buildApplicationApprovedPersonalisation()
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

            val request = SendNotifyApplicationApprovedMessage(
                language = Language.EN,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.APPLICATION_MINUS_APPROVED,
                toAddress = toAddress,
                personalisation = personalisation,
            )

            // When
            val notification = mapper.fromApprovedMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(Language.EN)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.APPLICATION_MINUS_APPROVED)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.VOTER_MINUS_CARD)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
        }
    }

    @Nested
    inner class FromRejectedMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "CY, WELSH",
                "EN, ENGLISH",
            ]
        )
        fun `should map SQS SendNotifyApplicationRejectedMessage to SendNotificationRequestDto`(
            language: Language,
            expectedLanguage: LanguageDto
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildApplicationRejectedPersonalisation()

            val request = SendNotifyApplicationRejectedMessage(
                language = language,
                sourceType = SqsSourceType.VOTER_MINUS_CARD,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.APPLICATION_MINUS_REJECTED,
                toAddress = toAddress,
                personalisation = personalisation,
            )

            val expectedToAddress = aNotificationDestination()
            val expectedChannel = NotificationChannel.LETTER
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = NotificationType.APPLICATION_REJECTED

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

            // When
            val notification = mapper.fromRejectedMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.language).isEqualTo(expectedLanguage)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)

            verify(languageMapper).fromMessageToDto(request.language)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(request.messageType)
            verify(sourceTypeMapper).fromMessageToDto(request.sourceType)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(request.toAddress)
        }
    }

    @Nested
    inner class FromRejectedDocumentMessageToSendNotificationRequestDto {
        @Test
        fun `should map SQS SendNotifyRejectedDocumentMessage to SendNotificationRequestDto`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildRejectedDocumentsPersonalisation()

            val request = SendNotifyRejectedDocumentMessage(
                language = Language.EN,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.REJECTED_MINUS_DOCUMENT,
                toAddress = toAddress,
                personalisation = personalisation,
                channel = SqsChannel.EMAIL
            )

            val expectedToAddress = aNotificationDestination()
            val expectedLanguage = LanguageDto.ENGLISH
            val expectedChannel = NotificationChannel.EMAIL
            val expectedSourceType = SourceType.POSTAL
            val expectedNotificationType = NotificationType.REJECTED_DOCUMENT

            given(languageMapper.fromMessageToDto(request.language)).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(request.messageType)).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(request.sourceType)).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(request.toAddress)).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(SqsChannel.EMAIL)).willReturn(expectedChannel)

            // When
            val notification = mapper.fromRejectedDocumentMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(expectedChannel)
            assertThat(notification.language).isEqualTo(expectedLanguage)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        }
    }

    @Nested
    inner class FromSendNotifyRejectedSignatureMessageToRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,EMAIL,ENGLISH",
                "EMAIL,CY,EMAIL,WELSH",
                "LETTER,EN,LETTER,ENGLISH",
                "LETTER,CY,LETTER,WELSH",
            ]
        )
        fun `should map SQS SendNotifyRejectedSignatureMessage to SendNotificationRequestDto with rejection reasons and notes`(
            sqsChannel: SqsChannel,
            language: Language,
            notificationChannel: NotificationChannel,
            languageDto: LanguageDto
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()

            val expectedSourceType = SourceType.PROXY
            val expectedNotificationType = REJECTED_SIGNATURE
            val rejectionReasons = listOf("Reason1", "Reason2")
            val rejectionNotes = "Invalid Signature"
            val personalisationMessage = buildRejectedSignaturePersonalisation(
                rejectionReasons = rejectionReasons,
                rejectionNotes = rejectionNotes
            )

            given(notificationTypeMapper.mapMessageTypeToNotificationType(MessageType.REJECTED_MINUS_SIGNATURE)).willReturn(
                REJECTED_SIGNATURE
            )
            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(notificationChannel)

            val request = buildSendNotifyRejectedSignatureMessage(
                channel = sqsChannel,
                language = language,
                sourceType = SqsSourceType.PROXY,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                toAddress = toAddress,
                personalisation = personalisationMessage,
            )

            // When
            val notification = mapper.fromRejectedSignatureMessageToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(notificationChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.PROXY)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }

    @Nested
    inner class FromNinoNotMatchedMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,EMAIL,ENGLISH",
                "EMAIL,CY,EMAIL,WELSH",
                "LETTER,EN,LETTER,ENGLISH",
                "LETTER,CY,LETTER,WELSH",
            ]
        )
        fun `should map SQS SendNotifyNinoNotMatchedMessage to SendNotificationRequestDto`(
            sqsChannel: SqsChannel,
            language: Language,
            notificationChannel: NotificationChannel,
            languageDto: LanguageDto
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildNinoNotMatchedPersonalisation()

            val expectedToAddress = aNotificationDestination()
            val expectedSourceType = SourceType.POSTAL
            val expectedNotificationType = NINO_NOT_MATCHED

            given(notificationTypeMapper.mapMessageTypeToNotificationType(MessageType.NINO_MINUS_NOT_MINUS_MATCHED)).willReturn(
                NINO_NOT_MATCHED
            )
            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(notificationChannelMapper.fromMessagingApiToDto(any())).willReturn(notificationChannel)

            val request = SendNotifyNinoNotMatchedMessage(
                language = language,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.NINO_MINUS_NOT_MINUS_MATCHED,
                toAddress = toAddress,
                personalisation = personalisation,
                channel = sqsChannel
            )

            val notification = mapper.fromNinoNotMatchedMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(notificationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.POSTAL)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.POSTAL)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(notificationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }
}

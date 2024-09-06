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
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_SIGNATURE
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REQUESTED_SIGNATURE
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.CommunicationChannelMapper
import uk.gov.dluhc.notificationsapi.mapper.DocumentCategoryMapper
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyBespokeCommMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyNotRegisteredToVoteMessage
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildBespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentRequiredPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildNotRegisteredToVotePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocumentsPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRequestedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedSignatureMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRequestedSignatureMessage
import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel as SqsChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: SendNotifyMessageMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationDestinationDtoMapper: NotificationDestinationDtoMapper

    @Mock
    private lateinit var documentCategoryMapper: DocumentCategoryMapper

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
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = PHOTO_RESUBMISSION
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = null,
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

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
            verify(communicationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }

        @Test
        fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto given rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = PHOTO_RESUBMISSION_WITH_REASONS
            val personalisationMessage = buildPhotoPersonalisationMessage(
                photoRejectionReasons = listOf(PhotoRejectionReason.OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO),
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

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
            verify(communicationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
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
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
            val personalisationMessage = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = emptyList(),
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

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
            verify(communicationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
        }

        @Test
        fun `should map SQS SendNotifyIdDocumentResubmissionMessage to SendNotificationRequestDto given document rejection reasons`() {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            val personalisationMessage = buildIdDocumentPersonalisationMessage(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                    ),
                ),
            )
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

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
            verify(communicationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
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
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.VOTER_CARD
            val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
            val personalisationMessage = buildIdDocumentRequiredPersonalisationMessage()
            val expectedLanguage = LanguageDto.ENGLISH

            given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(expectedChannel)

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
            verify(communicationChannelMapper).fromMessagingApiToDto(SqsChannel.EMAIL)
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
            val expectedChannel = CommunicationChannel.EMAIL
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
            val expectedChannel = CommunicationChannel.EMAIL
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
            ],
        )
        fun `should map SQS SendNotifyApplicationRejectedMessage to SendNotificationRequestDto`(
            language: Language,
            expectedLanguage: LanguageDto,
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
            val expectedChannel = CommunicationChannel.LETTER
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
                channel = SqsChannel.EMAIL,
            )

            val expectedToAddress = aNotificationDestination()
            val expectedLanguage = LanguageDto.ENGLISH
            val expectedChannel = CommunicationChannel.EMAIL
            val expectedSourceType = SourceType.POSTAL
            val expectedNotificationType = NotificationType.REJECTED_DOCUMENT

            given(languageMapper.fromMessageToDto(request.language)).willReturn(expectedLanguage)
            given(sourceTypeMapper.fromMessageToDto(request.sourceType)).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(request.toAddress)).willReturn(
                expectedToAddress,
            )
            given(communicationChannelMapper.fromMessagingApiToDto(SqsChannel.EMAIL)).willReturn(expectedChannel)
            given(documentCategoryMapper.fromApiMessageToDto(any())).willReturn(DocumentCategoryDto.IDENTITY)
            given(documentCategoryMapper.fromRejectedDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                expectedNotificationType,
            )

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

        @ParameterizedTest
        @CsvSource(
            value = [
                "EN,EMAIL,EMAIL,IDENTITY,IDENTITY,ENGLISH,REJECTED_DOCUMENT",
                "EN,LETTER,LETTER,IDENTITY,IDENTITY,ENGLISH,REJECTED_DOCUMENT",
                "CY,EMAIL,EMAIL,IDENTITY,IDENTITY,WELSH,REJECTED_DOCUMENT",
                "CY,LETTER,LETTER,IDENTITY,IDENTITY,WELSH,REJECTED_DOCUMENT",

                "EN,EMAIL,EMAIL,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,ENGLISH,REJECTED_PREVIOUS_ADDRESS",
                "EN,LETTER,LETTER,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,ENGLISH,REJECTED_PREVIOUS_ADDRESS",
                "CY,EMAIL,EMAIL,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,WELSH,REJECTED_PREVIOUS_ADDRESS",
                "CY,LETTER,LETTER,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,WELSH,REJECTED_PREVIOUS_ADDRESS",

                "EN,EMAIL,EMAIL,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,ENGLISH,REJECTED_PARENT_GUARDIAN",
                "EN,LETTER,LETTER,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,ENGLISH,REJECTED_PARENT_GUARDIAN",
                "CY,EMAIL,EMAIL,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,WELSH,REJECTED_PARENT_GUARDIAN",
                "CY,LETTER,LETTER,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,WELSH,REJECTED_PARENT_GUARDIAN",
            ],
        )
        fun `should map SQS SendNotifyNinoNotMatchedMessage to SendNotificationRequestDto`(
            language: Language,
            communicationChannel: CommunicationChannel,
            communicationChannelMessage: SqsChannel,
            documentCategory: DocumentCategory,
            documentCategoryDto: DocumentCategoryDto,
            languageDto: LanguageDto,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildRejectedDocumentsPersonalisation()

            val expectedToAddress = aNotificationDestination()

            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(SourceType.OVERSEAS)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)
            given(documentCategoryMapper.fromApiMessageToDto(any())).willReturn(documentCategoryDto)
            given(documentCategoryMapper.fromRejectedDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                expectedNotificationType,
            )

            val request = SendNotifyRejectedDocumentMessage(
                language = language,
                sourceType = SqsSourceType.OVERSEAS,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.REJECTED_MINUS_DOCUMENT,
                toAddress = toAddress,
                personalisation = personalisation,
                channel = communicationChannelMessage,
                documentCategory = documentCategory,
            )

            val notification =
                mapper.fromRejectedDocumentMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.OVERSEAS)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.OVERSEAS)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(communicationChannelMessage)
            verify(documentCategoryMapper).fromApiMessageToDto(documentCategory)
            verify(documentCategoryMapper).fromRejectedDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)
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
            ],
        )
        fun `should map SQS SendNotifyRejectedSignatureMessage to SendNotificationRequestDto with rejection reasons and notes`(
            sqsChannel: SqsChannel,
            language: Language,
            communicationChannel: CommunicationChannel,
            languageDto: LanguageDto,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()

            val expectedSourceType = SourceType.PROXY
            val expectedNotificationType = REJECTED_SIGNATURE
            val personalisationMessage = buildRejectedSignaturePersonalisation()

            given(notificationTypeMapper.mapMessageTypeToNotificationType(MessageType.REJECTED_MINUS_SIGNATURE)).willReturn(
                REJECTED_SIGNATURE,
            )
            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)

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
            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.PROXY)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }

    @Nested
    inner class FromSendNotifyRequestedSignatureMessageToRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,EMAIL,ENGLISH",
                "EMAIL,CY,EMAIL,WELSH",
                "LETTER,EN,LETTER,ENGLISH",
                "LETTER,CY,LETTER,WELSH",
            ],
        )
        fun `should map SQS SendNotifyRequestedSignatureMessage to SendNotificationRequestDto`(
            sqsChannel: SqsChannel,
            language: Language,
            communicationChannel: CommunicationChannel,
            languageDto: LanguageDto,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val expectedToAddress = aNotificationDestination()

            val expectedSourceType = SourceType.PROXY
            val expectedNotificationType = REQUESTED_SIGNATURE
            val personalisationMessage = buildRequestedSignaturePersonalisation()

            given(notificationTypeMapper.mapMessageTypeToNotificationType(MessageType.REQUESTED_MINUS_SIGNATURE))
                .willReturn(REQUESTED_SIGNATURE)
            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)

            val request = buildSendNotifyRequestedSignatureMessage(
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
            val notification = mapper.fromRequestedSignatureToSendNotificationRequestDto(request)

            // Then
            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(expectedSourceType)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.PROXY)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }

    @Nested
    inner class FromRequiredDocumentMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,false,EMAIL,ENGLISH,NINO_NOT_MATCHED",
                "EMAIL,CY,false,EMAIL,WELSH,NINO_NOT_MATCHED",
                "LETTER,EN,false,LETTER,ENGLISH,NINO_NOT_MATCHED",
                "LETTER,CY,false,LETTER,WELSH,NINO_NOT_MATCHED",
                "EMAIL,EN,true,EMAIL,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
                "EMAIL,CY,true,EMAIL,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
                "LETTER,EN,true,LETTER,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
                "LETTER,CY,true,LETTER,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",

            ],
        )
        fun `should map SQS SendNotifyNinoNotMatchedMessage to SendNotificationRequestDto with restrictedDocuments`(
            sqsChannel: SqsChannel,
            language: Language,
            hasRestrictedDocumentsList: Boolean,
            communicationChannel: CommunicationChannel,
            languageDto: LanguageDto,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildNinoNotMatchedPersonalisation()

            val expectedToAddress = aNotificationDestination()
            val expectedSourceType = SourceType.POSTAL

            val expectedNotificationTypeMappingToRun =
                expectedNotificationType != NotificationType.NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST

            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)
            given(documentCategoryMapper.fromApiMessageToDto(any())).willReturn(DocumentCategoryDto.IDENTITY)
            if (expectedNotificationTypeMappingToRun) {
                given(documentCategoryMapper.fromRequiredDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                    expectedNotificationType,
                )
            }

            val request = SendNotifyNinoNotMatchedMessage(
                language = language,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.NINO_MINUS_NOT_MINUS_MATCHED,
                toAddress = toAddress,
                personalisation = personalisation,
                channel = sqsChannel,
                hasRestrictedDocumentsList = hasRestrictedDocumentsList,
            )

            val notification =
                mapper.fromRequiredDocumentMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.POSTAL)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.POSTAL)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(sqsChannel)
            verify(documentCategoryMapper).fromApiMessageToDto(DocumentCategory.IDENTITY)
            if (expectedNotificationTypeMappingToRun) {
                verify(documentCategoryMapper).fromRequiredDocumentCategoryDtoToNotificationTypeDto(DocumentCategoryDto.IDENTITY)
            }
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "EN,EMAIL,EMAIL,IDENTITY,IDENTITY,ENGLISH,NINO_NOT_MATCHED",
                "EN,LETTER,LETTER,IDENTITY,IDENTITY,ENGLISH,NINO_NOT_MATCHED",
                "CY,EMAIL,EMAIL,IDENTITY,IDENTITY,WELSH,NINO_NOT_MATCHED",
                "CY,LETTER,LETTER,IDENTITY,IDENTITY,WELSH,NINO_NOT_MATCHED",

                "EN,EMAIL,EMAIL,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,ENGLISH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED",
                "EN,LETTER,LETTER,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,ENGLISH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED",
                "CY,EMAIL,EMAIL,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,WELSH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED",
                "CY,LETTER,LETTER,PREVIOUS_MINUS_ADDRESS,PREVIOUS_ADDRESS,WELSH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED",

                "EN,EMAIL,EMAIL,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,ENGLISH,PARENT_GUARDIAN_PROOF_REQUIRED",
                "EN,LETTER,LETTER,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,ENGLISH,PARENT_GUARDIAN_PROOF_REQUIRED",
                "CY,EMAIL,EMAIL,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,WELSH,PARENT_GUARDIAN_PROOF_REQUIRED",
                "CY,LETTER,LETTER,PARENT_MINUS_GUARDIAN,PARENT_GUARDIAN,WELSH,PARENT_GUARDIAN_PROOF_REQUIRED",
            ],
        )
        fun `should map SQS SendNotifyNinoNotMatchedMessage to SendNotificationRequestDto`(
            language: Language,
            communicationChannel: CommunicationChannel,
            communicationChannelMessage: SqsChannel,
            documentCategory: DocumentCategory,
            documentCategoryDto: DocumentCategoryDto,
            languageDto: LanguageDto,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildNinoNotMatchedPersonalisation()

            val expectedToAddress = aNotificationDestination()

            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(SourceType.OVERSEAS)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)
            given(documentCategoryMapper.fromApiMessageToDto(any())).willReturn(documentCategoryDto)
            given(documentCategoryMapper.fromRequiredDocumentCategoryDtoToNotificationTypeDto(any())).willReturn(
                expectedNotificationType,
            )

            val request = SendNotifyNinoNotMatchedMessage(
                language = language,
                sourceType = SqsSourceType.OVERSEAS,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                messageType = MessageType.NINO_MINUS_NOT_MINUS_MATCHED,
                toAddress = toAddress,
                personalisation = personalisation,
                channel = communicationChannelMessage,
                documentCategory = documentCategory,
                hasRestrictedDocumentsList = false,
            )

            val notification =
                mapper.fromRequiredDocumentMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.OVERSEAS)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.OVERSEAS)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(communicationChannelMessage)
            verify(documentCategoryMapper).fromApiMessageToDto(documentCategory)
            verify(documentCategoryMapper).fromRequiredDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)
        }
    }

    @Nested
    inner class FromBespokeCommMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,EMAIL,ENGLISH,BESPOKE_COMM",
                "EMAIL,CY,EMAIL,WELSH,BESPOKE_COMM",
                "LETTER,EN,LETTER,ENGLISH,BESPOKE_COMM",
                "LETTER,CY,LETTER,WELSH,BESPOKE_COMM",
            ],
        )
        fun `should map SQS SendNotifyBespokeCommMessage to SendNotificationRequestDto`(
            sqsChannel: SqsChannel,
            language: Language,
            communicationChannel: CommunicationChannel,
            languageDto: LanguageDto,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildBespokeCommPersonalisation()

            val expectedToAddress = aNotificationDestination()
            val expectedSourceType = SourceType.POSTAL

            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)

            val request = SendNotifyBespokeCommMessage(
                channel = sqsChannel,
                personalisation = personalisation,
                language = language,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                toAddress = toAddress,
                messageType = MessageType.BESPOKE_MINUS_COMM,
            )

            val notification = mapper.fromBespokeCommMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.POSTAL)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.POSTAL)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(request.messageType)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }

    @Nested
    inner class FromNotRegisteredToVoteMessageToSendNotificationRequestDto {
        @ParameterizedTest
        @CsvSource(
            value = [
                "EMAIL,EN,EMAIL,ENGLISH,NOT_REGISTERED_TO_VOTE",
                "EMAIL,CY,EMAIL,WELSH,NOT_REGISTERED_TO_VOTE",
                "LETTER,EN,LETTER,ENGLISH,NOT_REGISTERED_TO_VOTE",
                "LETTER,CY,LETTER,WELSH,NOT_REGISTERED_TO_VOTE",
            ],
        )
        fun `should map SQS SendNotifyNotRegisteredToVoteCommMessage to SendNotificationRequestDto`(
            sqsChannel: SqsChannel,
            language: Language,
            communicationChannel: CommunicationChannel,
            languageDto: LanguageDto,
            expectedNotificationType: NotificationType,
        ) {
            // Given
            val gssCode = aGssCode()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val toAddress = aMessageAddress()
            val personalisation = buildNotRegisteredToVotePersonalisation()

            val expectedToAddress = aNotificationDestination()
            val expectedSourceType = SourceType.POSTAL

            given(languageMapper.fromMessageToDto(any())).willReturn(languageDto)
            given(sourceTypeMapper.fromMessageToDto(any())).willReturn(expectedSourceType)
            given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
            given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)
            given(communicationChannelMapper.fromMessagingApiToDto(any())).willReturn(communicationChannel)

            val request = SendNotifyNotRegisteredToVoteMessage(
                channel = sqsChannel,
                personalisation = personalisation,
                language = language,
                sourceType = SqsSourceType.POSTAL,
                sourceReference = sourceReference,
                gssCode = gssCode,
                requestor = requestor,
                toAddress = toAddress,
                messageType = MessageType.NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE,
            )

            val notification = mapper.fromNotRegisteredToVoteMessageToSendNotificationRequestDto(request)

            assertThat(notification.channel).isEqualTo(communicationChannel)
            assertThat(notification.sourceType).isEqualTo(SourceType.POSTAL)
            assertThat(notification.sourceReference).isEqualTo(sourceReference)
            assertThat(notification.gssCode).isEqualTo(gssCode)
            assertThat(notification.requestor).isEqualTo(requestor)
            assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
            assertThat(notification.toAddress).isEqualTo(expectedToAddress)
            verify(languageMapper).fromMessageToDto(language)
            verify(sourceTypeMapper).fromMessageToDto(SqsSourceType.POSTAL)
            verify(notificationTypeMapper).mapMessageTypeToNotificationType(request.messageType)
            verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
            verify(communicationChannelMapper).fromMessagingApiToDto(sqsChannel)
        }
    }
}

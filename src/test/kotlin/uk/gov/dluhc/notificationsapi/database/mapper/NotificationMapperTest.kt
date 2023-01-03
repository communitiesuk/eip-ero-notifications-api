package uk.gov.dluhc.notificationsapi.database.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseTemplateVersion
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum

@ExtendWith(MockitoExtension::class)
internal class NotificationMapperTest {

    @InjectMocks
    private lateinit var mapper: NotificationMapperImpl

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map to NotifyDetails`() {
        // Given
        val notificationId = aNotificationId()
        val reference = aSourceReference()
        val templateId = aTemplateId()
        val templateVersion = aNotifySendEmailSuccessResponseTemplateVersion()
        val templateUri = aNotifySendEmailSuccessResponseTemplateUri(templateId)
        val body = aNotifySendEmailSuccessResponseBody()
        val subject = aNotifySendEmailSuccessResponseSubject()
        val fromEmail = anEmailAddress()

        val sendNotificationResponseDto = SendNotificationResponseDto(
            notificationId,
            reference,
            templateId,
            templateVersion,
            templateUri,
            body,
            subject,
            fromEmail
        )

        // When
        val actual = mapper.toNotifyDetails(sendNotificationResponseDto)

        // Then
        Assertions.assertThat(actual.notificationId).isEqualTo(notificationId)
        Assertions.assertThat(actual.reference).isEqualTo(reference)
        Assertions.assertThat(actual.templateId).isEqualTo(templateId)
        Assertions.assertThat(actual.templateVersion).isEqualTo(templateVersion)
        Assertions.assertThat(actual.templateUri).isEqualTo(templateUri)
        Assertions.assertThat(actual.body).isEqualTo(body)
        Assertions.assertThat(actual.subject).isEqualTo(subject)
        Assertions.assertThat(actual.fromEmail).isEqualTo(fromEmail)
    }

    @Test
    fun `should create notification`() {
        // Given
        val notificationId = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceType = SourceType.VOTER_CARD
        val expectedSourceType = SourceTypeEntityEnum.VOTER_CARD
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val notificationType = PHOTO_RESUBMISSION
        val expectedNotificationType = NotificationType.PHOTO_RESUBMISSION
        val personalisationDto = buildPhotoPersonalisationDto()
        val request = buildSendNotificationRequestDto(
            gssCode = gssCode,
            requestor = requestor,
            sourceType = sourceType,
            sourceReference = sourceReference,
            emailAddress = emailAddress,
            notificationType = notificationType,
        )
        val personalisationMap = buildPhotoPersonalisationMapFromDto(personalisationDto)
        val sendNotificationResponseDto = aSendNotificationDto()
        val expectedNotifyDetails = aNotifyDetails(sendNotificationResponseDto)
        val sentAt = aLocalDateTime()

        given(notificationTypeMapper.toNotificationTypeEntity(any())).willReturn(NotificationType.PHOTO_RESUBMISSION)
        given(sourceTypeMapper.toSourceTypeEntity(any())).willReturn(expectedSourceType)

        // When
        val notification = mapper.createNotification(notificationId, request, personalisationMap, sendNotificationResponseDto, sentAt)

        // Then
        Assertions.assertThat(notification.id).isEqualTo(notificationId)
        Assertions.assertThat(notification.type).isEqualTo(expectedNotificationType)
        Assertions.assertThat(notification.gssCode).isEqualTo(gssCode)
        Assertions.assertThat(notification.requestor).isEqualTo(requestor)
        Assertions.assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        Assertions.assertThat(notification.sourceReference).isEqualTo(sourceReference)
        Assertions.assertThat(notification.toEmail).isEqualTo(emailAddress)
        Assertions.assertThat(notification.personalisation).isEqualTo(personalisationMap)
        Assertions.assertThat(notification.notifyDetails).isEqualTo(expectedNotifyDetails)
        Assertions.assertThat(notification.sentAt).isEqualTo(sentAt)

        verify(notificationTypeMapper).toNotificationTypeEntity(PHOTO_RESUBMISSION)
        verify(sourceTypeMapper).toSourceTypeEntity(SourceType.VOTER_CARD)
    }
}

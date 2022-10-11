package uk.gov.dluhc.notificationsapi.database.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateVersion
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildSendNotificationRequestDto

internal class NotificationMapperTest {

    private val mapper = NotificationMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_REJECTED, APPLICATION_REJECTED",
            "APPLICATION_APPROVED, APPLICATION_APPROVED",
            "PHOTO_RESUBMISSION, PHOTO_RESUBMISSION"
        ]
    )
    fun `should map DTO Notification Type to Entity Notification Type`(
        dtoType: NotificationType,
        expected: uk.gov.dluhc.notificationsapi.database.entity.NotificationType
    ) {
        // Given

        // When
        val actual = mapper.toNotificationType(dtoType)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD, VOTER_CARD",
        ]
    )
    fun `should map DTO Source Type to Entity Source Type`(dtoType: SourceType, expected: uk.gov.dluhc.notificationsapi.database.entity.SourceType) {
        // Given

        // When
        val actual = mapper.toSourceType(dtoType)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }

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
        val expectedSourceType = uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val notificationType = NotificationType.APPLICATION_APPROVED
        val expectedNotificationType = uk.gov.dluhc.notificationsapi.database.entity.NotificationType.APPLICATION_APPROVED
        val personalisation = aNotificationPersonalisationMap()
        val request = buildSendNotificationRequestDto(
            gssCode = gssCode,
            requestor = requestor,
            sourceType = sourceType,
            sourceReference = sourceReference,
            emailAddress = emailAddress,
            notificationType = notificationType,
            personalisation = personalisation,
        )
        val sendNotificationDto = aSendNotificationDto()
        val expectedNotifyDetails = aNotifyDetails(sendNotificationDto)
        val sentAt = aLocalDateTime()

        // When
        val notification = mapper.createNotification(notificationId, request, sendNotificationDto, sentAt)

        // Then
        Assertions.assertThat(notification.id).isEqualTo(notificationId)
        Assertions.assertThat(notification.type).isEqualTo(expectedNotificationType)
        Assertions.assertThat(notification.gssCode).isEqualTo(gssCode)
        Assertions.assertThat(notification.requestor).isEqualTo(requestor)
        Assertions.assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        Assertions.assertThat(notification.sourceReference).isEqualTo(sourceReference)
        Assertions.assertThat(notification.toEmail).isEqualTo(emailAddress)
        Assertions.assertThat(notification.personalisation).isEqualTo(personalisation)
        Assertions.assertThat(notification.notifyDetails).isEqualTo(expectedNotifyDetails)
        Assertions.assertThat(notification.sentAt).isEqualTo(sentAt)
    }
}

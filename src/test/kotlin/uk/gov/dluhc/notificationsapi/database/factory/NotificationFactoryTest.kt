package uk.gov.dluhc.notificationsapi.database.factory

import org.assertj.core.api.Assertions.assertThat
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
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as EntityNotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as EntitySourceType

internal class NotificationFactoryTest {

    private val factory = NotificationFactory()

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
        expected: EntityNotificationType
    ) {
        // Given

        // When
        val actual = factory.toNotificationType(dtoType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD, VOTER_CARD",
        ]
    )
    fun `should map DTO Source Type to Entity Source Type`(dtoType: SourceType, expected: EntitySourceType) {
        // Given

        // When
        val actual = factory.toSourceType(dtoType)

        // Then
        assertThat(actual).isEqualTo(expected)
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
        val actual = factory.toNotifyDetails(sendNotificationResponseDto)

        // Then
        assertThat(actual.notificationId).isEqualTo(notificationId)
        assertThat(actual.reference).isEqualTo(reference)
        assertThat(actual.templateId).isEqualTo(templateId)
        assertThat(actual.templateVersion).isEqualTo(templateVersion)
        assertThat(actual.templateUri).isEqualTo(templateUri)
        assertThat(actual.body).isEqualTo(body)
        assertThat(actual.subject).isEqualTo(subject)
        assertThat(actual.fromEmail).isEqualTo(fromEmail)
    }

    @Test
    fun `should create notification`() {
        // Given
        val notificationId = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceType = SourceType.VOTER_CARD
        val expectedSourceType = EntitySourceType.VOTER_CARD
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val notificationType = NotificationType.APPLICATION_APPROVED
        val expectedNotificationType = EntityNotificationType.APPLICATION_APPROVED
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
        val notification = factory.createNotification(notificationId, request, sendNotificationDto, sentAt)

        // Then
        assertThat(notification.id).isEqualTo(notificationId)
        assertThat(notification.type).isEqualTo(expectedNotificationType)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.toEmail).isEqualTo(emailAddress)
        assertThat(notification.personalisation).isEqualTo(personalisation)
        assertThat(notification.notifyDetails).isEqualTo(expectedNotifyDetails)
        assertThat(notification.sentAt).isEqualTo(sentAt)
    }
}

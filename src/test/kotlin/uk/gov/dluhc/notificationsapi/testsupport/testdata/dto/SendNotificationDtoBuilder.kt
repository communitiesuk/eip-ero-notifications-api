package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

fun buildSendNotificationDto(
    notificationId: UUID = aNotifySendEmailSuccessResponseId(),
    reference: String = aNotifySendEmailSuccessResponseReference(),
    templateId: UUID = aNotifySendEmailSuccessResponseTemplateId(),
    templateVersion: Int = aNotifySendEmailSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendEmailSuccessResponseTemplateUri(templateId),
    body: String = aNotifySendEmailSuccessResponseBody(),
    subject: String = aNotifySendEmailSuccessResponseSubject(),
    fromEmail: String? = aNotifySendEmailSuccessResponseFromEmail(),
): SendNotificationResponseDto =
    SendNotificationResponseDto(
        notificationId = notificationId,
        reference = reference,
        templateId = templateId,
        templateVersion = templateVersion,
        templateUri = templateUri,
        body = body,
        subject = subject,
        fromEmail = fromEmail,
    )

fun aSendNotificationDto() = buildSendNotificationDto()

fun aNotifySendEmailSuccessResponseId(): UUID = UUID.randomUUID()

fun aNotifySendEmailSuccessResponseReference(): String = UUID.randomUUID().toString()

fun aTemplateId(): UUID = UUID.randomUUID()

fun aNotifySendEmailSuccessResponseTemplateId(): UUID = aTemplateId()

fun aNotifySendEmailSuccessResponseTemplateVersion(): Int = ThreadLocalRandom.current().nextInt(1, 100)

fun aNotifySendEmailSuccessResponseTemplateUri(templateId: UUID): String = "https://www.notifications.service.gov.uk/services/137e13d7-6acd-4449-815e-de0eb0c083ba/templates/$templateId"

fun aNotifySendEmailSuccessResponseBody(): String = "Hello John..."

fun aNotifySendEmailSuccessResponseSubject(): String = "Application Photo Declined"

fun aNotifySendEmailSuccessResponseFromEmail(): String = "voter-card-application-support@valtech.com"

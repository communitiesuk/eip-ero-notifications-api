package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

fun buildSendNotificationDto(
    notificationId: UUID = aNotifySendSuccessResponseId(),
    reference: String = aNotifySendSuccessResponseReference(),
    templateId: UUID = aNotifySendSuccessResponseTemplateId(),
    templateVersion: Int = aNotifySendSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendSuccessResponseTemplateUri(templateId),
    body: String = aNotifySendSuccessResponseBody(),
    subject: String = aNotifySendSuccessResponseSubject(),
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

fun aNotifySendSuccessResponseId(): UUID = UUID.randomUUID()

fun aNotifySendSuccessResponseReference(): String = UUID.randomUUID().toString()

fun aTemplateId(): UUID = UUID.randomUUID()

fun aNotifySendSuccessResponseTemplateId(): UUID = aTemplateId()

fun aNotifySendSuccessResponseTemplateVersion(): Int = ThreadLocalRandom.current().nextInt(1, 100)

fun aNotifySendSuccessResponseTemplateUri(templateId: UUID): String = "https://www.notifications.service.gov.uk/services/137e13d7-6acd-4449-815e-de0eb0c083ba/templates/$templateId"

fun aNotifySendSuccessResponseBody(): String = "Hello John..."

fun aNotifySendSuccessResponseSubject(): String = "Application Photo Declined"

fun aNotifySendEmailSuccessResponseFromEmail(): String = "voter-card-application-support@valtech.com"

fun aNotifySendLetterSuccessResponsePostage(): String = "second"

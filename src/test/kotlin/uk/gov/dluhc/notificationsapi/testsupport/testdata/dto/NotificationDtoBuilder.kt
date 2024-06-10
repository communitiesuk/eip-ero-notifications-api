package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotifyDetailsDto
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime
import java.util.UUID

fun aNotificationDtoBuilder(
    id: UUID = aNotificationId(),
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    sourceType: SourceType = SourceType.VOTER_CARD,
    type: NotificationType = NotificationType.APPLICATION_APPROVED,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    toEmail: String = anEmailAddress(),
    toPostalAddress: PostalAddress? = null,
    requestor: String = aRequestor(),
    sentAt: LocalDateTime = aLocalDateTime(),
    personalisation: Map<String, Any> = emptyMap(),
    notifyDetailsDto: NotifyDetailsDto = aNotifyDetailsDto(),
): NotificationDto =
    NotificationDto(
        id = id,
        sourceReference = sourceReference,
        gssCode = gssCode,
        sourceType = sourceType,
        type = type,
        channel = channel,
        toEmail = toEmail,
        toPostalAddress = toPostalAddress,
        requestor = requestor,
        sentAt = sentAt,
        personalisation = personalisation,
        notifyDetails = notifyDetailsDto,
    )

fun aNotificationDto(): NotificationDto = aNotificationDtoBuilder()

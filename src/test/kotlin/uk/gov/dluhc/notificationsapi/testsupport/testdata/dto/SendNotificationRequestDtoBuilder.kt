package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anOverseasAddress

fun buildSendNotificationRequestDto(
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = aSourceType(),
    sourceReference: String = aSourceReference(),
    toAddress: NotificationDestinationDto = NotificationDestinationDto(
        emailAddress = anEmailAddress(), postalAddress = aPostalAddress(), overseasElectorAddress = anOverseasAddress()
    ),
    notificationType: NotificationType = aNotificationType(),
    channel: NotificationChannel = aNotificationChannel(),
    language: LanguageDto = LanguageDto.ENGLISH,
): SendNotificationRequestDto =
    SendNotificationRequestDto(
        gssCode = gssCode,
        requestor = requestor,
        sourceType = sourceType,
        sourceReference = sourceReference,
        toAddress = toAddress,
        notificationType = notificationType,
        channel = channel,
        language = language,
    )

fun aSendNotificationRequestDto() = buildSendNotificationRequestDto()

package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api

import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDto

fun buildSendNotificationRequestDto(
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = aSourceType(),
    sourceReference: String = aSourceReference(),
    emailAddress: String = anEmailAddress(),
    notificationType: NotificationType = aNotificationType(),
    channel: NotificationChannel = aNotificationChannel(),
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoResubmissionPersonalisationDto = buildPhotoResubmissionPersonalisationDto(),
    personalisation1: Map<String, String> = aNotificationPersonalisationMap(),
): SendNotificationRequestDto =
    SendNotificationRequestDto(
        gssCode = gssCode,
        requestor = requestor,
        sourceType = sourceType,
        sourceReference = sourceReference,
        emailAddress = emailAddress,
        notificationType = notificationType,
        channel = channel,
        language = language,
        personalisation = personalisation,
        personalisation1 = personalisation1,
    )

fun aSendNotificationRequestDto() = buildSendNotificationRequestDto()

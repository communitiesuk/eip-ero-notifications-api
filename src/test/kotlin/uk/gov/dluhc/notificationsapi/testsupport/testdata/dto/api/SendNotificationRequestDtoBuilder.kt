package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api

import uk.gov.dluhc.notificationsapi.domain.NotificationType
import uk.gov.dluhc.notificationsapi.domain.SendNotificationRequest
import uk.gov.dluhc.notificationsapi.domain.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun buildSendNotificationRequestDto(
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = aSourceType(),
    sourceReference: String = aSourceReference(),
    emailAddress: String = anEmailAddress(),
    notificationType: NotificationType = aNotificationType(),
    personalisation: Map<String, String> = aNotificationPersonalisationMap(),
): SendNotificationRequest =
    SendNotificationRequest(
        gssCode = gssCode,
        requestor = requestor,
        sourceType = sourceType,
        sourceReference = sourceReference,
        emailAddress = emailAddress,
        notificationType = notificationType,
        personalisation = personalisation,
    )

fun aSendNotificationRequestDto() = buildSendNotificationRequestDto()

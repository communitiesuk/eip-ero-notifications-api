package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedDocumentMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference

fun buildSendNotifyRejectedDocumentMessage(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: RejectedDocumentPersonalisation = buildRejectedDocumentPersonalisationMessage(),
    toAddress: MessageAddress = aMessageAddress(),
): SendNotifyRejectedDocumentMessage =
    SendNotifyRejectedDocumentMessage(
        channel = channel,
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.REJECTED_MINUS_DOCUMENT,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun aSendNotifyRejectedDocumentMessage() = buildSendNotifyRejectedDocumentMessage()

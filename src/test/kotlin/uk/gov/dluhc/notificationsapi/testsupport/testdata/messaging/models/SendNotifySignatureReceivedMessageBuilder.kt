package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifySignatureReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference

fun buildSendNotifySignatureReceivedMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: BasePersonalisation,
    toAddress: MessageAddress = aMessageAddress(),
) = SendNotifySignatureReceivedMessage(
    language = language,
    sourceType = sourceType,
    sourceReference = sourceReference,
    gssCode = gssCode,
    requestor = requestor,
    personalisation = personalisation,
    toAddress = toAddress,
    messageType = MessageType.SIGNATURE_MINUS_RECEIVED,
)

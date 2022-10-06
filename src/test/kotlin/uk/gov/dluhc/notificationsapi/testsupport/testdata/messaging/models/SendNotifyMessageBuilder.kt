package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Channel
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationInner
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun buildSendNotifyMessage(
    channel: Channel = Channel.EMAIL,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    messageType: MessageType = MessageType.APPLICATIONREJECTED,
    personalisation: List<TemplatePersonalisationInner> = listOf(TemplatePersonalisationInner(name = "applicant_name", value = "John")),
    toAddress: MessageAddress = MessageAddress(emailAddress = anEmailAddress()),
): SendNotifyMessage =
    SendNotifyMessage(
        channel = channel,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = messageType,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun aSendNotifyMessage() = buildSendNotifyMessage()

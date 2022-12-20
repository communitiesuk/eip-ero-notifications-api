package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationNameValue
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun buildSendNotifyMessage(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    messageType: MessageType = MessageType.APPLICATION_MINUS_REJECTED,
    personalisation: List<TemplatePersonalisationNameValue> = listOf(TemplatePersonalisationNameValue(name = "applicant_name", value = "John")),
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

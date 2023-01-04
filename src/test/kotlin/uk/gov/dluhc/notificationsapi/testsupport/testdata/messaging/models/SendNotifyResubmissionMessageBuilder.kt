package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun buildSendNotifyPhotoResubmissionMessage(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    messageType: MessageType = MessageType.PHOTO_MINUS_RESUBMISSION,
    personalisation: PhotoPersonalisation = buildPhotoPersonalisationMessage(),
    toAddress: MessageAddress = MessageAddress(emailAddress = anEmailAddress()),
): SendNotifyPhotoResubmissionMessage =
    SendNotifyPhotoResubmissionMessage(
        channel = channel,
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = messageType,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun buildSendNotifyIdDocumentResubmissionMessage(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    messageType: MessageType = MessageType.PHOTO_MINUS_RESUBMISSION,
    personalisation: IdDocumentPersonalisation = buildIdDocumentPersonalisationMessage(),
    toAddress: MessageAddress = MessageAddress(emailAddress = anEmailAddress()),
): SendNotifyIdDocumentResubmissionMessage =
    SendNotifyIdDocumentResubmissionMessage(
        channel = channel,
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = messageType,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun aSendNotifyPhotoResubmissionMessage() = buildSendNotifyPhotoResubmissionMessage()

fun aSendNotifyIdDocumentResubmissionMessage() = buildSendNotifyIdDocumentResubmissionMessage()

package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyNinoNotMatchedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyNinoNotMatchedMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: NinoNotMatchedPersonalisation = buildNinoNotMatchedPersonalisation(),
    channel: NotificationChannel = NotificationChannel.EMAIL,
    toAddress: MessageAddress = aMessageAddress(),
    hasRestrictedDocumentsList: Boolean = false,
    documentCategory: DocumentCategory = DocumentCategory.IDENTITY
): SendNotifyNinoNotMatchedMessage =
    SendNotifyNinoNotMatchedMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.NINO_MINUS_NOT_MINUS_MATCHED,
        personalisation = personalisation,
        channel = channel,
        toAddress = toAddress,
        hasRestrictedDocumentsList = hasRestrictedDocumentsList,
        documentCategory = documentCategory
    )

fun buildNinoNotMatchedPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    additionalNotes: String = faker.yoda().quote(),
): NinoNotMatchedPersonalisation =
    NinoNotMatchedPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        additionalNotes = additionalNotes,
    )

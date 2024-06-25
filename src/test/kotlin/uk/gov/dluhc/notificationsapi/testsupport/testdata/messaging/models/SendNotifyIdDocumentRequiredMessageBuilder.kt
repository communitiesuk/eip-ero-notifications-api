package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyIdDocumentRequiredMessage(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: IdDocumentRequiredPersonalisation = buildIdDocumentRequiredPersonalisationMessage(),
    toAddress: MessageAddress = aMessageAddress(),
): SendNotifyIdDocumentRequiredMessage =
    SendNotifyIdDocumentRequiredMessage(
        channel = channel,
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_REQUIRED,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun buildIdDocumentRequiredPersonalisationMessage(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequiredFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
): IdDocumentRequiredPersonalisation =
    IdDocumentRequiredPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequiredFreeText = idDocumentRequiredFreeText,
        eroContactDetails = eroContactDetails,
    )

fun aSendNotifyIdDocumentRequiredMessage() = buildSendNotifyIdDocumentRequiredMessage()

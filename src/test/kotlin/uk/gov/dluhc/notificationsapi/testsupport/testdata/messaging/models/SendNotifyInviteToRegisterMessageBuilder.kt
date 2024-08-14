package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.InviteToRegisterPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyInviteToRegisterMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyInviteToRegisterMessage(
        language: Language = Language.EN,
        sourceType: SourceType = SourceType.POSTAL,
        sourceReference: String = aSourceReference(),
        gssCode: String = aGssCode(),
        requestor: String = aRequestor(),
        channel: CommunicationChannel = CommunicationChannel.EMAIL,
        toAddress: MessageAddress = aMessageAddress(),
        personalisation: InviteToRegisterPersonalisation = buildInviteToRegisterPersonalisation()
): SendNotifyInviteToRegisterMessage =
    SendNotifyInviteToRegisterMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.INVITE_MINUS_TO_MINUS_REGISTER,
        personalisation = personalisation,
        channel = channel,
        toAddress = toAddress,
    )

fun buildInviteToRegisterPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    freeText: String? = DataFaker.faker.yoda().quote(),
): InviteToRegisterPersonalisation =
    InviteToRegisterPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        freeText = freeText
    )

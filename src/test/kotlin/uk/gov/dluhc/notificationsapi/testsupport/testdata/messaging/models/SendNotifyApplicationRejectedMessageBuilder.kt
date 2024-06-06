package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyApplicationRejectedMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: ApplicationRejectedPersonalisation = buildApplicationRejectedPersonalisation(),
    toAddress: MessageAddress = aMessageAddress(),
): SendNotifyApplicationRejectedMessage =
    SendNotifyApplicationRejectedMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.APPLICATION_MINUS_REJECTED,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun buildApplicationRejectedPersonalisation(
    rejectionReasonList: List<ApplicationRejectionReason> = listOf(
        INCOMPLETE_MINUS_APPLICATION,
        NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT,
        OTHER,
    ),
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    rejectionReasonMessage: String = "Application rejected for incomplete information and no response from the applicant",
): ApplicationRejectedPersonalisation =
    ApplicationRejectedPersonalisation(
        rejectionReasonList = rejectionReasonList,
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        rejectionReasonMessage = rejectionReasonMessage,
    )

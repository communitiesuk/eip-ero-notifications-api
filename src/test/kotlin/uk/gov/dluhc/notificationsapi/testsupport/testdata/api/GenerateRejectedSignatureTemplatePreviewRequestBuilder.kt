package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateRejectedSignatureTemplatePreviewRequest(
    sourceType: SourceType = SourceType.PROXY,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    personalisation: RejectedSignaturePersonalisation = buildRejectedSignaturePersonalisation(),
    language: Language? = Language.EN,
) = GenerateRejectedSignatureTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation,
)

fun buildRejectedSignaturePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    rejectionNotes: String? = "Invalid Signature",
    rejectionReasons: List<SignatureRejectionReason> = listOf(
        SignatureRejectionReason.PARTIALLY_MINUS_CUT_MINUS_OFF,
        SignatureRejectionReason.TOO_MINUS_DARK,
        SignatureRejectionReason.OTHER,
    ),
    rejectionFreeText: String? = null,
) = RejectedSignaturePersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionNotes = rejectionNotes,
    rejectionReasons = rejectionReasons,
    rejectionFreeText = rejectionFreeText,
)

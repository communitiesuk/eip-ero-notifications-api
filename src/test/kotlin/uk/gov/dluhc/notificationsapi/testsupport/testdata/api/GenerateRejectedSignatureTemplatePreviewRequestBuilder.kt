package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateRejectedSignatureTemplatePreviewRequest(
    sourceType: SourceType = SourceType.PROXY,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    personalisation: RejectedSignaturePersonalisation = buildRejectedSignaturePersonalisation(),
    language: Language? = Language.EN,
) = GenerateRejectedSignatureTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = language,
    personalisation = personalisation
)

fun buildRejectedSignaturePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    rejectionNotes: String? = "Invalid Signature",
    rejectionReasons: List<SignatureRejectionReason> = listOf(
        SignatureRejectionReason.TOO_MINUS_SMALL_MINUS_OR_MINUS_UNREADABLE,
        SignatureRejectionReason.IMAGE_MINUS_NOT_MINUS_CLEAR,
        SignatureRejectionReason.OTHER
    ),
    rejectionFreeText: String? = null,
) = RejectedSignaturePersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionNotes = rejectionNotes,
    rejectionReasons = rejectionReasons,
    rejectionFreeText = rejectionFreeText
)

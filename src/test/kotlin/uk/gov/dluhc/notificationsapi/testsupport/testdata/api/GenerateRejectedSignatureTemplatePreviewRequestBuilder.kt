package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails

fun buildGenerateRejectedSignatureTemplatePreviewRequest(
    sourceType: SourceType = SourceType.PROXY,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    personalisation: RejectedSignaturePersonalisation = buildRejectedSignaturePersonalisation()
) = GenerateRejectedSignatureTemplatePreviewRequest(
    channel = channel,
    sourceType = sourceType,
    language = Language.EN,
    personalisation = personalisation
)

fun buildRejectedSignaturePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    rejectionNotes: String? = null,
    rejectionReasons: List<String> = emptyList()
) = RejectedSignaturePersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionNotes = rejectionNotes,
    rejectionReasons = rejectionReasons
)
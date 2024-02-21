package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedParentGuardianTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.models.RejectedParentGuardianPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest

fun buildRejectedParentGuardianTemplatePreviewRequest(
    personalisation: RejectedParentGuardianPersonalisation = buildRejectedParentGuardianRequiredPersonalisation(),
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel = NotificationChannel.EMAIL
): GenerateRejectedParentGuardianTemplatePreviewRequest =
    GenerateRejectedParentGuardianTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel
    )

fun buildRejectedParentGuardianRequiredPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(localAuthorityName = "Barcelona"),
    documents: List<RejectedDocument> = listOf(buildRejectedDocument()),
    rejectedDocumentFreeText: String? = null
): RejectedParentGuardianPersonalisation =
    RejectedParentGuardianPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        documents = documents,
        rejectedDocumentFreeText = rejectedDocumentFreeText
    )

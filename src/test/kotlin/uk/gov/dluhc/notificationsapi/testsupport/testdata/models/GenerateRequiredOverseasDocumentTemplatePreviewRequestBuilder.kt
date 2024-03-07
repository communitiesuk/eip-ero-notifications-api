package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.models.GenerateRequiredOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.RequiredOverseasDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest

fun buildRequiredOverseasDocumentTemplatePreviewRequest(
    personalisation: RequiredOverseasDocumentPersonalisation = buildRequiredOverseasDocumentPersonalisation(),
    language: Language = Language.EN,
    documentCategory: DocumentCategory = DocumentCategory.PARENT_MINUS_GUARDIAN,
    channel: NotificationChannel = NotificationChannel.EMAIL
): GenerateRequiredOverseasDocumentTemplatePreviewRequest =
    GenerateRequiredOverseasDocumentTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        documentCategory = documentCategory,
        channel = channel
    )

fun buildRequiredOverseasDocumentPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(
        localAuthorityName = "Barcelona",
        address = buildAddressRequest()
    ),
    requiredDocumentFreeText: String? = null
): RequiredOverseasDocumentPersonalisation =
    RequiredOverseasDocumentPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        requiredDocumentFreeText = requiredDocumentFreeText
    )

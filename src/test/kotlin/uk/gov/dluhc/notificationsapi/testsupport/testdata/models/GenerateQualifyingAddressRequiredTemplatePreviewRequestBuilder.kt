package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateQualifyingAddressRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.QualifyingAddressRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest

fun buildQualifyingAddressRequiredTemplateRequest(
    personalisation: QualifyingAddressRequiredPersonalisation = buildQualifyingAddressRequiredPersonalisation(),
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel = NotificationChannel.EMAIL
): GenerateQualifyingAddressRequiredTemplatePreviewRequest =
    GenerateQualifyingAddressRequiredTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel
    )

fun buildQualifyingAddressRequiredPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(localAuthorityName = "Barcelona"),
    freeText: String? = null
): QualifyingAddressRequiredPersonalisation =
    QualifyingAddressRequiredPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        freeText = freeText
    )

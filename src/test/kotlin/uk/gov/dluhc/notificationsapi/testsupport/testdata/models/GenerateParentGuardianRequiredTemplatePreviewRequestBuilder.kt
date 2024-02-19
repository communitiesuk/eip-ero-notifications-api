package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateParentGuardianRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.ParentGuardianRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest

fun buildParentGuardianTemplatePreviewRequest(
    personalisation: ParentGuardianRequiredPersonalisation = buildParentGuardianRequiredPersonalisation(),
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel = NotificationChannel.EMAIL
): GenerateParentGuardianRequiredTemplatePreviewRequest =
    GenerateParentGuardianRequiredTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel
    )

fun buildParentGuardianRequiredPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(localAuthorityName = "Barcelona"),
    freeText: String? = null
): ParentGuardianRequiredPersonalisation =
    ParentGuardianRequiredPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        freeText = freeText
    )

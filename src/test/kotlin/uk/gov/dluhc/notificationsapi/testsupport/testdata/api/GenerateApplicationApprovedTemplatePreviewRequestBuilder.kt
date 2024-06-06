package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationApprovedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildGenerateApplicationApprovedTemplatePreviewRequest(
    language: Language = Language.EN,
    personalisation: BasePersonalisation = buildBasePersonalisation(),
    sourceType: SourceType,
): GenerateApplicationApprovedTemplatePreviewRequest =
    GenerateApplicationApprovedTemplatePreviewRequest(
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
    )

fun buildBasePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(),
): BasePersonalisation =
    BasePersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
    )
